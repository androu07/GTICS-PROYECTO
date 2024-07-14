package com.example.webapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name="codigo_colegio")
public class CodigoColegio implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_codigo_colegio")
    private int id;
    @Column(nullable = false)
    private String nombre;
}
