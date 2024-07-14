package com.example.webapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Entity
@Getter
@Setter
@Table(name="tarjetas")
public class Tarjeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_tarjeta")
    private int id;
    @Column(nullable = false)
    private BigInteger numero;
    private String mes_caduca;
    private String anhio_caduca;
    private Integer cvv;
    private Double ahorros;
}
