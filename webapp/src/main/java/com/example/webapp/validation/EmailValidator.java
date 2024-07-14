package com.example.webapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

    private static final String EMAIL_PATTERN =
            "^[^\\s@]+@[^\\s@]+\\.(com|edu\\.pe)$";

    @Override
    public void initialize(ValidEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("¡Este no puede quedar vacío!")
                    .addConstraintViolation();
            return false;
        }

        if (email.length() > 35) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Longitud maxima de 35 caracteres")
                    .addConstraintViolation();
            return false;
        }

        if (email.contains(" ") || email.matches(".*[áéíóúÁÉÍÓÚñÑ].*") || email.matches(".*[!#$%&'*+/=?^_`{|}~].*")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("El correo no puede contener caracteres no permitidos")
                    .addConstraintViolation();
            return false;
        }

        if (!Pattern.compile(EMAIL_PATTERN).matcher(email).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("El correo debe tener un formato correcto")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
