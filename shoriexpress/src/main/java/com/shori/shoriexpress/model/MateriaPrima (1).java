package com.shori.shoriexpress.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "materia_prima")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MateriaPrima {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_materia_prima")
    private Long idMateriaPrima;

    @NotBlank(message = "El nombre de la materia prima es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(name = "nombre_materia_prima", nullable = false, length = 100)
    private String nombreMateriaPrima;

    @NotBlank(message = "La categoría es obligatoria")
    @Size(max = 50, message = "La categoría no puede exceder 50 caracteres")
    @Column(name = "categoria_materia_prima", nullable = false, length = 50)
    private String categoriaMateriaPrima;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_medida_materia_prima")
    private UnidadMedida unidadMedidaMateriaPrima;

    @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
    @Column(name = "descripcion_materia_prima", length = 200)
    private String descripcionMateriaPrima;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Column(name = "precio_materia_prima", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioMateriaPrima;

    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(name = "stock_materia_prima")
    private Integer stockMateriaPrima = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_materia_prima", nullable = false)
    private EstadoMateriaPrima estadoMateriaPrima = EstadoMateriaPrima.disponible;

    @Column(name = "fecha_registro_materia_prima")
    private LocalDateTime fechaRegistroMateriaPrima;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @Column(name = "fecha_vencimiento_materia_prima", nullable = false)
    private LocalDate fechaVencimientoMateriaPrima;

    @PrePersist
    protected void onCreate() {
        fechaRegistroMateriaPrima = LocalDateTime.now();
    }

    public enum UnidadMedida {
        kg, g, unidad, ml, l
    }

    public enum EstadoMateriaPrima {
        disponible, reservada, en_preparación, agotada, caducada, 
        en_mal_estado, devuelta, pendiente_de_ingreso, bloqueada, 
        preparada, en_proceso_de_descongelación
    }
}