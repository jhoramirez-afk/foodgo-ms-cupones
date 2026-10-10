package cl.duoc.jv0101.foodgo.cupones.service;

import java.util.List;
import cl.duoc.jv0101.foodgo.cupones.exception.ResourceConflictException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cl.duoc.jv0101.foodgo.cupones.exception.ResourceNotFoundException;
import cl.duoc.jv0101.foodgo.cupones.model.UsoCupon;
import cl.duoc.jv0101.foodgo.cupones.model.Cupon;
import cl.duoc.jv0101.foodgo.cupones.repository.UsoCuponRepository;
import cl.duoc.jv0101.foodgo.cupones.repository.CuponRepository;

@Service
@Transactional
public class UsoCuponService {

    private final UsoCuponRepository repository;
    private final CuponRepository cuponRepository;

    public UsoCuponService(UsoCuponRepository repository, CuponRepository cuponRepository) {
        this.repository = repository;
        this.cuponRepository = cuponRepository;
    }

    @Transactional(readOnly = true)
    public List<UsoCupon> findByCuponId(Long cuponId) {
        if (!cuponRepository.existsById(cuponId)) {
            throw new ResourceNotFoundException("Cupon no encontrado con id " + cuponId);
        }
        return repository.findByCupon_Id(cuponId);
    }

    @Transactional(readOnly = true)
    public UsoCupon findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UsoCupon no encontrado con id " + id));
    }

    public UsoCupon create(Long cuponId, UsoCupon recurso) {
        Cupon cupon = cuponRepository.findById(cuponId)
                .orElseThrow(() -> new ResourceNotFoundException("Cupon no encontrado con id " + cuponId));
        if (repository.existsByCupon_IdAndPedidoId(cuponId, recurso.getPedidoId())) {
            throw new ResourceConflictException("Este cupón ya fue utilizado en ese pedido");
        }
        recurso.setId(null);
        recurso.setCupon(cupon);
        recurso.setMontoDescontado(CuponService.calcularDescuento(cupon, recurso.getSubtotalPedido()));
        return repository.save(recurso);
    }

    public UsoCupon update(Long id, UsoCupon datos) {
        UsoCupon existente = findById(id);
        if (repository.existsByCupon_IdAndPedidoIdAndIdNot(existente.getCupon().getId(), datos.getPedidoId(), id)) {
            throw new ResourceConflictException("Este cupón ya fue utilizado en ese pedido");
        }
        existente.setPedidoId(datos.getPedidoId());
        existente.setFechaUso(datos.getFechaUso());
        existente.setSubtotalPedido(datos.getSubtotalPedido());
        existente.setMontoDescontado(CuponService.calcularDescuento(existente.getCupon(), datos.getSubtotalPedido()));
        return repository.save(existente);
    }

    public void delete(Long id) {
        UsoCupon existente = findById(id);
        repository.delete(existente);
    }
}
