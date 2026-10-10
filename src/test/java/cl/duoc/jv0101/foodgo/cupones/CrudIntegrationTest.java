package cl.duoc.jv0101.foodgo.cupones;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CrudIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    private String unique(String json) { return json.replace("TEST20261009", UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase()); }

    private long createParent() throws Exception {
        String result = mvc.perform(post("/api/cupones").contentType("application/json")
                .content(unique("""
{"codigo":"BARRIO10-TEST20261009","tipo":"PORCENTAJE","descuento":10}
"""))).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(result).get("id").asLong();
    }

    private long createChild(String nested) throws Exception {
        String result = mvc.perform(post(nested).contentType("application/json")
                .content("""
{"pedidoId":1,"fechaUso":"2026-10-01T13:30:00","subtotalPedido":19980}
""")).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(result).get("id").asLong();
    }

    @Test
    void crudRelationsAndCascadeThroughHttp() throws Exception {
        long id = createParent();
        String nested = "/api/cupones/" + id + "/usos";
        long childId = createChild(nested);
        mvc.perform(get("/api/cupones/" + id)).andExpect(status().isOk()).andExpect(jsonPath("$.usos[0].id").value(childId));
        mvc.perform(get("/api/cupones")).andExpect(status().isOk());
        mvc.perform(get(nested)).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(childId));
        mvc.perform(get("/api/usos/" + childId)).andExpect(status().isOk());
        mvc.perform(put("/api/cupones/" + id).contentType("application/json").content(unique("""
{"codigo":"BARRIO10-TEST20261009","tipo":"PORCENTAJE","descuento":10}
"""))).andExpect(status().isOk());
        mvc.perform(put("/api/usos/" + childId).contentType("application/json").content("""
{"pedidoId":1,"fechaUso":"2026-10-01T13:35:00","subtotalPedido":29970}
""")).andExpect(status().isOk());
        mvc.perform(delete("/api/usos/" + childId)).andExpect(status().isNoContent());
        mvc.perform(get("/api/usos/" + childId)).andExpect(status().isNotFound());
        long cascadeId = createChild(nested);
        mvc.perform(delete("/api/cupones/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/cupones/" + id)).andExpect(status().isNotFound());
        mvc.perform(get("/api/usos/" + cascadeId)).andExpect(status().isNotFound());
    }

    @Test
    void malformedJsonAndIdsReturnStructuredErrors() throws Exception {
        mvc.perform(post("/api/cupones").contentType("application/json").content("{"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        mvc.perform(get("/api/cupones/no-es-numero"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void validationAndMissingResourcesReturnStructuredErrors() throws Exception {
        mvc.perform(post("/api/cupones").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.validationErrors").isNotEmpty());
        mvc.perform(get("/api/cupones/9223372036854775807"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));
        mvc.perform(post("/api/cupones/9223372036854775807/usos").contentType("application/json")
                .content("""
{"pedidoId":1,"fechaUso":"2026-10-01T13:30:00","subtotalPedido":19980}
""")).andExpect(status().isNotFound());
        var longBody = (com.fasterxml.jackson.databind.node.ObjectNode) mapper.readTree("""
{"codigo":"BARRIO10-TEST20261009","tipo":"PORCENTAJE","descuento":10}
""");
        longBody.put("codigo", "X".repeat(300));
        mvc.perform(post("/api/cupones").contentType("application/json").content(longBody.toString())).andExpect(status().isBadRequest());
    }

    static Stream<Arguments> invalidInputs() {
        return Stream.of(
            Arguments.of("Porcentaje superior a 100", "parent", """
{"codigo":"BARRIO10-TEST20261009","tipo":"PORCENTAJE","descuento":101}
""", "porcentajeValido"),
            Arguments.of("Descuento cero", "parent", """
{"codigo":"BARRIO10-TEST20261009","tipo":"PORCENTAJE","descuento":0}
""", "descuento"),
            Arguments.of("Tipo inválido", "parent", """
{"codigo":"BARRIO10-TEST20261009","tipo":"REGALO","descuento":10}
""", "tipo"),
            Arguments.of("Código inválido", "parent", """
{"codigo":"barrio 10","tipo":"PORCENTAJE","descuento":10}
""", "codigo"),
            Arguments.of("Subtotal obligatorio", "child", """
{"pedidoId":1,"fechaUso":"2026-10-01T13:30:00","subtotalPedido":null}
""", "subtotalPedido"),
            Arguments.of("Subtotal cero", "child", """
{"pedidoId":1,"fechaUso":"2026-10-01T13:30:00","subtotalPedido":0}
""", "subtotalPedido"),
            Arguments.of("Pedido inválido", "child", """
{"pedidoId":0,"fechaUso":"2026-10-01T13:30:00","subtotalPedido":19980}
""", "pedidoId")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidInputs")
    void businessValidationReturns400WithField(String name, String target, String body, String field) throws Exception {
        if ("parent".equals(target)) {
            mvc.perform(post("/api/cupones").contentType("application/json").content(unique(body)))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.validationErrors." + field).exists());
        } else {
            long id = createParent();
            mvc.perform(post("/api/cupones/" + id + "/usos").contentType("application/json").content(body))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.validationErrors." + field).exists());
            mvc.perform(delete("/api/cupones/" + id)).andExpect(status().isNoContent());
        }
    }

    @Test
    void descuentoCalculadoDuplicadosYPoliticaDeCuponUtilizado() throws Exception {
        long id = createParent();
        String nested = "/api/cupones/" + id + "/usos";
        String body = """
{"pedidoId":1,"fechaUso":"2026-10-01T13:30:00","subtotalPedido":19980,"montoDescontado":1}
""";
        String result = mvc.perform(post(nested).contentType("application/json").content(body))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.montoDescontado").value(1998)).andReturn().getResponse().getContentAsString();
        long uso = mapper.readTree(result).get("id").asLong();
        mvc.perform(post(nested).contentType("application/json").content(body)).andExpect(status().isConflict());
        String original = mvc.perform(get("/api/cupones/" + id)).andReturn().getResponse().getContentAsString();
        var cambio = (com.fasterxml.jackson.databind.node.ObjectNode) mapper.readTree(original);
        cambio.remove("usos"); cambio.put("descuento", 20);
        mvc.perform(put("/api/cupones/" + id).contentType("application/json").content(cambio.toString())).andExpect(status().isConflict());
        mvc.perform(put("/api/usos/" + uso).contentType("application/json").content("""
{"pedidoId":1,"fechaUso":"2026-10-01T13:35:00","subtotalPedido":29970}
"""))
                .andExpect(status().isOk()).andExpect(jsonPath("$.montoDescontado").value(2997));
        mvc.perform(delete("/api/cupones/" + id)).andExpect(status().isNoContent());
    }

    @Test
    void montoFijoNoSuperaSubtotalYCodigoEsUnico() throws Exception {
        String body = """
{"codigo":"FIJO2000-TEST","tipo":"MONTO_FIJO","descuento":2000}
""";
        String result = mvc.perform(post("/api/cupones").contentType("application/json").content(body)).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = mapper.readTree(result).get("id").asLong();
        mvc.perform(post("/api/cupones").contentType("application/json").content(body)).andExpect(status().isConflict());
        mvc.perform(post("/api/cupones/" + id + "/usos").contentType("application/json").content("""
{"pedidoId":1,"fechaUso":"2026-10-01T13:30:00","subtotalPedido":1000}
"""))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.validationErrors.subtotalPedido").exists());
        mvc.perform(post("/api/cupones/" + id + "/usos").contentType("application/json").content("""
{"pedidoId":1,"fechaUso":"2026-10-01T13:30:00","subtotalPedido":19980}
"""))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.montoDescontado").value(2000));
        mvc.perform(delete("/api/cupones/" + id)).andExpect(status().isNoContent());
    }

}
