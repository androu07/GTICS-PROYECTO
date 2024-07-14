package com.example.webapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DniValidator implements ConstraintValidator<ValidDni, Integer> {

    @Override
    public void initialize(ValidDni constraintAnnotation) {
    }

    @Override
    public boolean isValid(Integer dni, ConstraintValidatorContext context) {
        if (dni == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("El DNI no puede quedar vacío")
                    .addConstraintViolation();
            return false;
        }

        String dniString = String.format("%08d", dni);
        if (dniString.length() != 8 || !dniString.matches("\\d{8}")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("El DNI debe tener exactamente 8 dígitos y no puede contener letras ni caracteres especiales")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
