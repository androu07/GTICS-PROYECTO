package com.example.webapp.entity;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Data {

    private String dni;
    private String nombres;
    private String apellido_paterno;
    private String apellido_materno;
    private String caracter_verificacion;
    private String caracter_verificacion_anterior;

}
