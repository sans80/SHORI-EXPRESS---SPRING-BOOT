package com.shori.shoriexpress.model;

import java.time.LocalDateTime;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bono")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bono {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bono")
    private Long idBono;

    @Min(value = 0, message = "Los puntos acumulados no pueden ser negativos")
    @Column(name = "puntos_acumulados_bono")
    private Integer puntosAcumuladosBono = 0;

    @Min(value = 1, message = "Los puntos necesarios deben ser al menos 1")
    @Column(name = "puntos_necesarios_bono")
    private Integer puntosNecesariosBono = 5;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_bono")
    private EstadoBono estadoBono = EstadoBono.no_disponible;

    @Column(name = "fecha_actualizacion_bono")
    private LocalDateTime fechaActualizacionBono;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacionBono = LocalDateTime.now();
    }

    public enum EstadoBono {
        disponible, redimido, no_disponible
    }
}