package com.example.webapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="medicamentos_recojo")
public class MedicamentoRecojo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idmedicamentos_recojo")
    private int id;
    @Column(nullable = false)
    private String nombre_medicamento;
    private String costo_medicamento;
    private int cantidad;

    @ManyToOne
    @JoinColumn(name = "pedidos_paciente_recojo_idpedidos_paciente_recojo")
    private PedidosPaciente pedidosPaciente;

    @ManyToOne
    @JoinColumn(name = "pedidos_paciente_recojo_usuario_id_usuario")
    private Usuario usuario;
}
