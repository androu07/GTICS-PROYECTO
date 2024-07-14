package com.example.webapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="medicamentos_del_pedido")
public class MedicamentosDelPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_medicamentos_del_pedido")
    private int id;
    @Column(nullable = false)
    private String nombre_medicamento;
    private String costo_medicamento;
    private int cantidad;

    @ManyToOne
    @JoinColumn(name = "pedidos_paciente_idpedidos_paciente")
    private PedidosPaciente pedidosPaciente;

    @ManyToOne
    @JoinColumn(name = "pedidos_paciente_usuario_id_usuario")
    private Usuario usuario;
}
