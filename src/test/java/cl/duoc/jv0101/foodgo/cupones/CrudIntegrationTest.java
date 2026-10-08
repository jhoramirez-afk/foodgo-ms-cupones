package cl.duoc.jv0101.foodgo.cupones;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CrudIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired cl.duoc.jv0101.foodgo.cupones.repository.UsoCuponRepository children;

    @Test
    void crudRelationsAndCascadeThroughHttp() throws Exception {
        String created = mvc.perform(post("/api/cupones").contentType("application/json")
                .content("""
{"codigo": "EP02DEMO", "tipo": "PORCENTAJE", "descuento": 10}
"""))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long parentId = mapper.readTree(created).get("id").asLong();
        String nested = "/api/cupones/%s/usos".formatted(parentId);
        String child = mvc.perform(post(nested).contentType("application/json")
                .content("""
{"pedidoId": 1, "fechaUso": "2026-10-06T20:00:00", "montoDescontado": 10.0}
"""))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long childId = mapper.readTree(child).get("id").asLong();
        mvc.perform(get("/api/cupones/" + parentId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.usos[0].id").value(childId));
        mvc.perform(get("/api/cupones")).andExpect(status().isOk());
        mvc.perform(get(nested)).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(childId));
        mvc.perform(get("/api/usos/" + childId)).andExpect(status().isOk());
        mvc.perform(put("/api/cupones/" + parentId).contentType("application/json")
                .content("""
{"codigo": "EP02DEMO actualizado", "tipo": "PORCENTAJE", "descuento": 10}
""")).andExpect(status().isOk());
        mvc.perform(put("/api/usos/" + childId).contentType("application/json")
                .content("""
{"pedidoId": 1, "fechaUso": "2026-10-06T20:00:00", "montoDescontado": 10.0}
""")).andExpect(status().isOk());
        mvc.perform(delete("/api/usos/" + childId)).andExpect(status().isNoContent());
        mvc.perform(get("/api/usos/" + childId)).andExpect(status().isNotFound());
        String second = mvc.perform(post(nested).contentType("application/json")
                .content("""
{"pedidoId": 1, "fechaUso": "2026-10-06T20:00:00", "montoDescontado": 10.0}
"""))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long cascadeId = mapper.readTree(second).get("id").asLong();
        mvc.perform(delete("/api/cupones/" + parentId)).andExpect(status().isNoContent());
        assertThat(children.existsById(cascadeId)).isFalse();
        mvc.perform(get("/api/cupones/" + parentId)).andExpect(status().isNotFound());
    }

    @Test
    void malformedJsonAndIdsReturnStructuredErrors() throws Exception {
        mvc.perform(post("/api/cupones").contentType("application/json").content("{"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        mvc.perform(get("/api/cupones/no-es-numero"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void databaseConstraintReturnsConflict() throws Exception {
        mvc.perform(post("/api/cupones").contentType("application/json")
                .content("""
{"codigo": "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX", "tipo": "PORCENTAJE", "descuento": 10}
"""))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void validationAndMissingResourcesReturnStructuredErrors() throws Exception {
        mvc.perform(post("/api/cupones").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        mvc.perform(get("/api/cupones/9223372036854775807"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));
        mvc.perform(post("/api/cupones/%s/usos".formatted(Long.MAX_VALUE)).contentType("application/json")
                .content("""
{"pedidoId": 1, "fechaUso": "2026-10-06T20:00:00", "montoDescontado": 10.0}
""")).andExpect(status().isNotFound());
    }
}
