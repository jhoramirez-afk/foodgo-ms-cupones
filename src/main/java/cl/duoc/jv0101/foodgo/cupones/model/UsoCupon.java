package cl.duoc.jv0101.foodgo.cupones.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

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
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "usos_cupon", uniqueConstraints = @jakarta.persistence.UniqueConstraint(
        name = "uk_cupon_pedido", columnNames = {"cupon_id", "pedido_id"}))
public class UsoCupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Pedido es obligatorio")
    @Positive(message = "Pedido debe ser mayor que cero")
    @Column(nullable = false)
    private Long pedidoId;

    @NotNull(message = "Fecha de uso es obligatoria")
    @PastOrPresent(message = "La fecha no puede estar en el futuro")
    @Column(nullable = false)
    private LocalDateTime fechaUso;

    @com.fasterxml.jackson.annotation.JsonProperty(access = com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY)
    @Column(precision = 9, scale = 0)
    private BigDecimal montoDescontado;

    @NotNull(message = "Subtotal del pedido es obligatorio")
    @DecimalMin(value = "1", message = "Subtotal del pedido debe ser mayor que cero")
    @Digits(integer = 9, fraction = 0, message = "El importe debe expresarse en pesos CLP enteros, hasta 9 dígitos")
    @Column(precision = 9, scale = 0)
    private BigDecimal subtotalPedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cupon_id", nullable = false)
    @JsonBackReference("cupon-usos")
    private Cupon cupon;

    public BigDecimal getSubtotalPedido() { return subtotalPedido; }

    public void setSubtotalPedido(BigDecimal subtotalPedido) { this.subtotalPedido = subtotalPedido; }

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
