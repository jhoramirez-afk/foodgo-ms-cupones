package cl.duoc.jv0101.foodgo.cupones;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import cl.duoc.jv0101.foodgo.cupones.model.Cupon;
import cl.duoc.jv0101.foodgo.cupones.repository.CuponRepository;
import cl.duoc.jv0101.foodgo.cupones.service.CuponService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CuponServiceTest {

    @Mock
    private CuponRepository repository;

    @InjectMocks
    private CuponService service;

    private Cupon recurso() {
        Cupon r = new Cupon();
        r.setId(1L);
        r.setCodigo("Demo");
        r.setTipo("valor");
        r.setDescuento(BigDecimal.TEN);
        return r;
    }

    @Test
    void listarRetornaTodos() {
        when(repository.findAll()).thenReturn(List.of(recurso()));
        assertThat(service.findAll()).hasSize(1);
    }

    @Test
    void buscarPorIdExistente() {
        when(repository.findById(1L)).thenReturn(Optional.of(recurso()));
        assertThat(service.findById(1L)).isPresent();
    }

    @Test
    void buscarPorIdInexistente() {
        when(repository.findById(9L)).thenReturn(Optional.empty());
        assertThat(service.findById(9L)).isEmpty();
    }

    @Test
    void crearGuarda() {
        when(repository.save(any())).thenReturn(recurso());
        assertThat(service.create(recurso()).getCodigo()).isEqualTo("Demo");
    }

    @Test
    void actualizarExistente() {
        Cupon datos = recurso();
        datos.setCodigo("Actualizado");
        when(repository.findById(1L)).thenReturn(Optional.of(recurso()));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        Optional<Cupon> resultado = service.update(1L, datos);
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getCodigo()).isEqualTo("Actualizado");
    }

    @Test
    void actualizarInexistente() {
        when(repository.findById(9L)).thenReturn(Optional.empty());
        assertThat(service.update(9L, recurso())).isEmpty();
    }

    @Test
    void eliminarExistente() {
        when(repository.findById(1L)).thenReturn(Optional.of(recurso()));
        assertThat(service.delete(1L)).isTrue();
        verify(repository).delete(any());
    }

    @Test
    void eliminarInexistente() {
        when(repository.findById(9L)).thenReturn(Optional.empty());
        assertThat(service.delete(9L)).isFalse();
    }
}
