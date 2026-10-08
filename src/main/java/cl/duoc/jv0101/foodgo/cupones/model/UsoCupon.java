package cl.duoc.jv0101.foodgo.cupones.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "usos_cupon")
public class UsoCupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Pedido es obligatorio")
    @Column(nullable = false)
    private Long pedidoId;

    @NotNull(message = "FechaUso es obligatorio")
    @Column(nullable = false)
    private LocalDateTime fechaUso;

    @DecimalMin(value = "0.0", inclusive = true, message = "El valor no puede ser negativo")
    @Column
    private BigDecimal montoDescontado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cupon_id", nullable = false)
    @JsonBackReference("cupon-usos")
    private Cupon cupon;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public LocalDateTime getFechaUso() {
        return fechaUso;
    }

    public void setFechaUso(LocalDateTime fechaUso) {
        this.fechaUso = fechaUso;
    }

    public BigDecimal getMontoDescontado() {
        return montoDescontado;
    }

    public void setMontoDescontado(BigDecimal montoDescontado) {
        this.montoDescontado = montoDescontado;
    }

    public Cupon getCupon() {
        return cupon;
    }

    public void setCupon(Cupon cupon) {
        this.cupon = cupon;
    }
}
