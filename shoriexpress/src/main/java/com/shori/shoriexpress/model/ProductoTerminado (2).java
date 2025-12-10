package com.shori.shoriexpress.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "producto_terminado")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoTerminado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto_terminado")
    private Long idProductoTerminado;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    @Column(name = "cantidad_producto_terminado", nullable = false)
    private Integer cantidadProductoTerminado;

    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(name = "stock_producto_terminado")
    private Integer stockProductoTerminado = 0;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(name = "nombre_producto_terminado", nullable = false, length = 100)
    private String nombreProductoTerminado;

    @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
    @Column(name = "descripcion_producto_terminado", length = 200)
    private String descripcionProductoTerminado;

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Column(name = "precio_venta_producto_terminado", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVentaProductoTerminado;
}