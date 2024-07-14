package com.example.webapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="pedidos_reposicion")
public class PedidosReposicion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_pedidos_reposicion")
    private int id;
    @ManyToOne
    @JoinColumn(name="usuario_id_usuario")
    private Usuario usuario;

    @Column(nullable = false)
    private String fecha_solicitud;
    private double costo_total;
    private String fecha_entrega;
    private String estado_de_reposicion;
    @ManyToOne
    @JoinColumn(name="proveedor_id_proveedor")
    private Proveedor proveedor;
}

