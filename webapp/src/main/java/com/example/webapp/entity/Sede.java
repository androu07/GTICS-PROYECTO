package com.example.webapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="sede")
public class Sede {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_sede")
    private int id;
    @Column(nullable = false)
    private String nombre;
    private String estado;
}
