package com.shori.shoriexpress.model;

import java.time.LocalDateTime;
import java.util.Optional;

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
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @NotNull(message = "El tipo de documento es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento_usuario", nullable = false)
    private TipoDocumento tipoDocumentoUsuario;

    @NotBlank(message = "El documento es obligatorio")
    @Size(max = 10, message = "El documento no puede exceder 10 caracteres")
    @Column(name = "documento_usuario", unique = true, nullable = false, length = 10)
    private String documentoUsuario;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    @Column(name = "nombre_usuario", unique = true, nullable = false, length = 50)
    private String nombreUsuario;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(name = "contraseña_usuario", nullable = false, length = 255)
    private String contraseñaUsuario;

    @NotBlank(message = "El primer nombre es obligatorio")
    @Size(min = 2, max = 40, message = "El primer nombre debe tener entre 2 y 40 caracteres")
    @Column(name = "primer_nombre_usuario", nullable = false, length = 40)
    private String primerNombreUsuario;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 40, message = "El apellido debe tener entre 2 y 40 caracteres")
    @Column(name = "apellido_usuario", nullable = false, length = 40)
    private String apellidoUsuario;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe ser válido")
    @Size(max = 100, message = "El correo no puede exceder 100 caracteres")
    @Column(name = "correo_usuario", unique = true, nullable = false, length = 100)
    private String correoUsuario;

    @Column(name = "fecha_registro_usuario")
    private LocalDateTime fechaRegistroUsuario;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    @Column(name = "telefono_usuario", length = 20)
    private String telefonoUsuario;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 100, message = "La dirección no puede exceder 100 caracteres")
    @Column(name = "direccion_usuario", nullable = false, length = 100)
    private String direccionUsuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_usuario")
    private EstadoUsuario estadoUsuario = EstadoUsuario.activo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @PrePersist
    protected void onCreate() {
        fechaRegistroUsuario = LocalDateTime.now();
    }

    public enum TipoDocumento {
        CC, TI, PET, PPT, Pasaporte
    }

    public enum EstadoUsuario {
        activo, inactivo
    }

    // Método para obtener el nombre completo
    public String getNombreCompleto() {
        return primerNombreUsuario + " " + apellidoUsuario;
    }

    // Setter personalizado para manejar el tipo de documento desde String
    public void setTipoDocumentoUsuario(String tipoDocumento) {
        if (tipoDocumento != null && !tipoDocumento.isEmpty()) {
            this.tipoDocumentoUsuario = TipoDocumento.valueOf(tipoDocumento);
        }
    }

    // Setter personalizado para manejar el tipo de documento desde enum
    public void setTipoDocumentoUsuario(TipoDocumento tipoDocumento) {
        this.tipoDocumentoUsuario = tipoDocumento;
    }

    // Método para establecer el nombre completo separándolo en primer nombre y apellido
    public void setNombreCompleto(String nombreCompleto) {
        if (nombreCompleto != null && !nombreCompleto.trim().isEmpty()) {
            String[] partes = nombreCompleto.trim().split("\\s+", 2);
            if (partes.length >= 1) {
                this.primerNombreUsuario = partes[0];
                if (partes.length >= 2) {
                    this.apellidoUsuario = partes[1];
                } else {
                    // Si solo hay una palabra, usar como primer nombre y poner un apellido por defecto
                    this.apellidoUsuario = "Sin Apellido";
                }
            }
        }
    }
}