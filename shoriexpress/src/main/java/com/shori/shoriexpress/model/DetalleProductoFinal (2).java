package com.shori.shoriexpress.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalle_producto_final")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleProductoFinal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_producto_final")
    private Long idDetalleProductoFinal;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_materia_prima", nullable = false)
    @NotNull(message = "La materia prima es obligatoria")
    private MateriaPrima materiaPrima;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto_terminado", nullable = false)
    @NotNull(message = "El producto terminado es obligatorio")
    private ProductoTerminado productoTerminado;

    @NotNull(message = "La cantidad utilizada es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(name = "cantidad_utilizada", nullable = false)
    private Integer cantidadUtilizada;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_medida_deta")
    private UnidadMedidaDeta unidadMedidaDeta;

    @Column(name = "observaciones_detalle", columnDefinition = "TEXT")
    private String observacionesDetalle;

    public enum UnidadMedidaDeta {
        kg, g, unidad, ml, l
    }
}