package com.example.webapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="pedidos_paciente_recojo")
public class PedidosPacienteRecojo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idpedidos_paciente_recojo")
    private int id;
    @Column(nullable = false)
    private String nombre_paciente;
    private String apellido_paciente;
    private String medico_que_atiende;
    private String seguro;
    private String dni;
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
    private String sede_de_recojo;
    private Integer telefono;
    private String mapa_tracking;

    private byte[] receta_foto;

    @ManyToOne
    @JoinColumn(name = "usuario_id_usuario")
    private Usuario usuario;
}
