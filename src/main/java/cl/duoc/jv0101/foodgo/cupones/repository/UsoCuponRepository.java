package cl.duoc.jv0101.foodgo.cupones.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.duoc.jv0101.foodgo.cupones.model.UsoCupon;

public interface UsoCuponRepository extends JpaRepository<UsoCupon, Long> {
    List<UsoCupon> findByCupon_Id(Long cuponId);
}
