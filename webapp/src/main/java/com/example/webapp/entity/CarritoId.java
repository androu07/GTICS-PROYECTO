package com.example.webapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class CarritoId implements Serializable {
    @Column(name = "usuario_id_usuario", nullable = false)
    private Integer usuario_id_usuario;

    @Column(name = "medicamentos_id_medicamentos", nullable = false)
    private Integer medicamentos_id_medicamentos;

}
