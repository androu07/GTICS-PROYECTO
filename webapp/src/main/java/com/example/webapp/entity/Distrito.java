package com.example.webapp.entity;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name="distrito")
public class Distrito implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_distrito")
    private int id;
    @Column(nullable = false)
    private String nombre;
}
