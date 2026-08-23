package cl.duoc.jv0101.foodgo.cupones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cl.duoc.jv0101.foodgo.cupones.model.Cupon;

public interface CuponRepository extends JpaRepository<Cupon, Long> {
}
