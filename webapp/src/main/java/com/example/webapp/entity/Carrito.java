package com.example.webapp.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="carrito")
public class Carrito {

    @EmbeddedId
    private CarritoId id;

    @MapsId("usuario_id_usuario")
    @OneToOne
    @JoinColumn(name="usuario_id_usuario")
    private Usuario usuario_id_usuario;

    @MapsId("medicamentos_id_medicamentos")
    @ManyToOne
    @JoinColumn(name="medicamentos_id_medicamentos")
    private Medicamentos medicamentos_id_medicamentos;

    @Column(nullable = false)
    private int cantidad;
    private String numero_pedido;
    private String estado_de_compra;
}

