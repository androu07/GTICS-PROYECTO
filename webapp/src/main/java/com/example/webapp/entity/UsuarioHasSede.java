package com.example.webapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="usuario_has_sede")
public class UsuarioHasSede {

    @EmbeddedId
    private UsuarioHasSedeId id;

    @MapsId("usuario_id_usuario")
    @ManyToOne
    @JoinColumn(name="usuario_id_usuario")
    private Usuario usuario_id_usario;

    @MapsId("sede_id_sede")
    @ManyToOne
    @JoinColumn(name="sede_id_sede")
    private Sede sede_id_sede;

}
