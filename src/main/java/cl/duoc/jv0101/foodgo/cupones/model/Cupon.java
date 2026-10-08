package cl.duoc.jv0101.foodgo.cupones.model;

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
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;


@Entity
@Table(name = "cupones")
public class Cupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código es obligatorio")
    @Column(nullable = false)
    private String codigo;
    @Column
    private String tipo;
    @Column
    private BigDecimal descuento;

    @Valid
    @OneToMany(mappedBy = "cupon", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("cupon-usos")
    private List<UsoCupon> usos = new ArrayList<>();

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
