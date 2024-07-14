package com.example.webapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name="proveedor")
public class Proveedor implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_proveedor")
    private int id;
    @Column(nullable = false)
    private String nombre;
}
