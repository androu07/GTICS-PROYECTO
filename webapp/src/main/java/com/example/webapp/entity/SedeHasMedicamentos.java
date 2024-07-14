package com.example.webapp.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="sede_has_medicamentos")
public class SedeHasMedicamentos {
    @EmbeddedId
    private SedeHasMedicamentosId id;

    @MapsId("id_sede")
    @ManyToOne
    @JoinColumn(name="sede_id_sede")
    private Sede id_sede;

    @MapsId("id_medicamentos")
    @ManyToOne
    @JoinColumn(name="medicamentos_id_medicamentos")
    private Medicamentos id_medicamentos;
}
