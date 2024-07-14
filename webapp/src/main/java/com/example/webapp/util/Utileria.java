package com.example.webapp.util;

import org.springframework.stereotype.Component;

@Component
public class Utileria {

    public  String GenerarToken() {
        String cadena = "0123456789ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        String token = "";
        int max = cadena.length() - 1;

        for (int i = 0; i < 40; i++) {
            int caracter = (int) (Math.random() * (max));
            token += cadena.charAt(caracter);
        }
        return token;
    }
    
}
