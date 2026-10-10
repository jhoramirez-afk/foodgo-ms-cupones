package cl.duoc.jv0101.foodgo.cupones.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;


@Entity
@Table(name = "cupones")
public class Cupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Código es obligatorio")
    @Pattern(regexp = "[A-Z0-9_-]{3,40}", message = "El código debe tener de 3 a 40 letras mayúsculas, números, guiones o guiones bajos")
    @Column(nullable = false, unique = true, length = 40)
    private String codigo;
    @NotBlank(message = "Tipo de cupón es obligatorio")
    @Pattern(regexp = "PORCENTAJE|MONTO_FIJO", message = "Tipo de cupón debe ser PORCENTAJE, MONTO_FIJO")
    @Column(nullable = false)
    private String tipo;
    @NotNull(message = "Descuento es obligatorio")
    @DecimalMin(value = "1", message = "Descuento debe ser mayor que cero")
    @Digits(integer = 9, fraction = 0, message = "El importe debe expresarse en pesos CLP enteros, hasta 9 dígitos")
    @Column(precision = 9, scale = 0)
    private BigDecimal descuento;

    @Valid
    @OneToMany(mappedBy = "cupon", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("cupon-usos")
    private List<UsoCupon> usos = new ArrayList<>();

    @AssertTrue(message = "El descuento porcentual no puede superar 100")
    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isPorcentajeValido() {
        return !"PORCENTAJE".equals(tipo) || descuento == null || descuento.compareTo(new BigDecimal("100")) <= 0;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }

    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getTipo() { return tipo; }

    public void setTipo(String tipo) { this.tipo = tipo; }

    public BigDecimal getDescuento() { return descuento; }

    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }

    public List<UsoCupon> getUsos() {
        return usos;
    }

    public void setUsos(List<UsoCupon> items) {
        this.usos.clear();
        if (items != null) {
            items.forEach(this::addUsoCupon);
        }
    }

    public void addUsoCupon(UsoCupon item) {
        usos.add(item);
        item.setCupon(this);
    }

    public void removeUsoCupon(UsoCupon item) {
        usos.remove(item);
        item.setCupon(null);
    }
}
