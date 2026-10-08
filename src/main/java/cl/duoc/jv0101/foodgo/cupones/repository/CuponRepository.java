package cl.duoc.jv0101.foodgo.cupones.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.duoc.jv0101.foodgo.cupones.model.Cupon;

public interface CuponRepository extends JpaRepository<Cupon, Long> {
    @Override
    @EntityGraph(attributePaths = "usos")
    List<Cupon> findAll();

    @Override
    @EntityGraph(attributePaths = "usos")
    Optional<Cupon> findById(Long id);
}
