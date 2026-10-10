package cl.duoc.jv0101.foodgo.cupones.service;

import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;
import cl.duoc.jv0101.foodgo.cupones.exception.BusinessRuleException;
import cl.duoc.jv0101.foodgo.cupones.exception.ResourceConflictException;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cl.duoc.jv0101.foodgo.cupones.model.Cupon;
import cl.duoc.jv0101.foodgo.cupones.repository.CuponRepository;

@Service
@Transactional
public class CuponService {

    private final CuponRepository repository;

    public CuponService(CuponRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Cupon> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Cupon> findById(Long id) {
        return repository.findById(id);
    }

    public Cupon create(Cupon recurso) {
        recurso.setId(null);
        recurso.getUsos().forEach(item -> item.setId(null));
        if (repository.existsByCodigoIgnoreCase(recurso.getCodigo())) {
            throw new ResourceConflictException("Ya existe un cupón con ese código");
        }
        long pedidosDistintos = recurso.getUsos().stream().map(uso -> uso.getPedidoId()).distinct().count();
        if (pedidosDistintos != recurso.getUsos().size()) {
            throw new ResourceConflictException("Un cupón no puede usarse dos veces en el mismo pedido");
        }
        recurso.getUsos().forEach(uso -> uso.setMontoDescontado(calcularDescuento(recurso, uso.getSubtotalPedido())));
        return repository.save(recurso);
    }

    public Optional<Cupon> update(Long id, Cupon datos) {
        return repository.findById(id).map(existente -> {
            if (repository.existsByCodigoIgnoreCaseAndIdNot(datos.getCodigo(), id)) {
                throw new ResourceConflictException("Ya existe un cupón con ese código");
            }
            if (!existente.getUsos().isEmpty() &&
                    (!existente.getTipo().equals(datos.getTipo()) || existente.getDescuento().compareTo(datos.getDescuento()) != 0)) {
                throw new ResourceConflictException("No se puede cambiar el descuento de un cupón que ya fue utilizado");
            }
            existente.setCodigo(datos.getCodigo());
            existente.setTipo(datos.getTipo());
            existente.setDescuento(datos.getDescuento());
            return repository.save(existente);
        });
    }

    public boolean delete(Long id) {
        return repository.findById(id).map(existente -> {
            repository.delete(existente);
            return true;
        }).orElse(false);
    }
    public static BigDecimal calcularDescuento(Cupon cupon, BigDecimal subtotal) {
        BigDecimal descuento = "PORCENTAJE".equals(cupon.getTipo())
                ? subtotal.multiply(cupon.getDescuento()).divide(new BigDecimal("100"), 0, RoundingMode.HALF_UP)
                : cupon.getDescuento();
        if (descuento.compareTo(subtotal) > 0) {
            throw new BusinessRuleException("subtotalPedido", "El descuento no puede superar el subtotal del pedido");
        }
        return descuento;
    }
}
