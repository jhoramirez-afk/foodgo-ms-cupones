package cl.duoc.jv0101.foodgo.cupones.service;

import java.util.List;
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
        return repository.save(recurso);
    }

    public Optional<Cupon> update(Long id, Cupon datos) {
        return repository.findById(id).map(existente -> {
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
}
