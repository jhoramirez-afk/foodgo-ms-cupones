package cl.duoc.jv0101.foodgo.cupones.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cl.duoc.jv0101.foodgo.cupones.model.UsoCupon;
import cl.duoc.jv0101.foodgo.cupones.service.UsoCuponService;

@RestController
@RequestMapping("/api")
public class UsoCuponController {

    private final UsoCuponService service;

    public UsoCuponController(UsoCuponService service) {
        this.service = service;
    }

    @GetMapping("/cupones/{cuponId}/usos")
    public ResponseEntity<List<UsoCupon>> listarPorCupon(@PathVariable Long cuponId) {
        return ResponseEntity.ok(service.findByCuponId(cuponId));
    }

    @PostMapping("/cupones/{cuponId}/usos")
    public ResponseEntity<UsoCupon> crear(@PathVariable Long cuponId, @Valid @RequestBody UsoCupon recurso) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(cuponId, recurso));
    }

    @GetMapping("/usos/{id}")
    public ResponseEntity<UsoCupon> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/usos/{id}")
    public ResponseEntity<UsoCupon> actualizar(@PathVariable Long id, @Valid @RequestBody UsoCupon datos) {
        return ResponseEntity.ok(service.update(id, datos));
    }

    @DeleteMapping("/usos/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
