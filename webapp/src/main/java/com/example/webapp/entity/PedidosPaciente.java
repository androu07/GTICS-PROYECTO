package com.example.webapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="pedidos_paciente")
public class PedidosPaciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idpedidos_paciente")
    private int id;
    @Column(nullable = false)
    private String nombre_paciente;
    private String apellido_paciente;
    private String dni;
    private String medico_que_atiende;
    private String seguro;
    private String distrito;
    private Double costo_total;
    private String tipo_de_pedido;
    private String fecha_solicitud;
    private String fecha_entrega;
    private String validacion_del_pedido;
    private String comentario;
    private String fecha_validacion;
    private String estado_del_pedido;
    private String numero_tracking;
    private String aviso_vencimiento;
    private String metodo_pago;
    private String mapa_tracking;

    private byte[] receta_foto;

    @NotBlank(message = "La dirección no puede quedar vacia")
    @Size(max = 90, message = "La dirección no puede tener más de 90 caracteres")
    private String direccion;

    private Integer telefono;

    @NotBlank(message = "La hora de entrega no puede quedar vacia")
    private String hora_de_entrega;

    @ManyToOne
    @JoinColumn(name = "usuario_id_usuario")
    private Usuario usuario;
}
