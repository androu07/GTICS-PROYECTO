package com.example.webapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class UsuarioHasSedeId implements Serializable {
    @Column(name = "usuario_id_usuario", nullable = false)
    private Integer usuario_id_usuario;

    @Column(name = "sede_id_sede", nullable = false)
    private Integer sede_id_sede;
}
