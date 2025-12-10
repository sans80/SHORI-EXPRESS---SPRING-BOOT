package com.shori.shoriexpress.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "redencion_bono")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedencionBono {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_redencion")
    private Long idRedencion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_bono", nullable = false)
    private Bono bono;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_redencion")
    private LocalDateTime fechaRedencion;

    @PrePersist
    protected void onCreate() {
        fechaRedencion = LocalDateTime.now();
    }
}