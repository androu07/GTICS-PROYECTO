package com.example.webapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="medicamentos")
public class Medicamentos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_medicamentos")
    private int id;
    @Column(nullable = false)
    @NotBlank
    @Size( max = 400, message = "La descripción no puede tener más de 400 caracteres")
    private String descripcion;
    @NotBlank
    @Size( max = 45, message = "El nombre del medicamento no puede tener más de 45 caracteres")
    private String nombre;
    private byte[] foto;

    @Positive(message = "El inventario debe ser positivo")
    private int inventario;

    @Positive(message = "El precio por unidad debe ser positivo")
    private double precio_unidad;
    private String fecha_ingreso;
    private String categoria;
    private String dosis;
    private int borrado_logico;
}