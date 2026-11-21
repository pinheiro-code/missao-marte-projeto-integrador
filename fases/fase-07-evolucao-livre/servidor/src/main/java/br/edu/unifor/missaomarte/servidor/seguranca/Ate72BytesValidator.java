package br.edu.unifor.missaomarte.servidor.seguranca;

import java.nio.charset.StandardCharsets;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class Ate72BytesValidator implements ConstraintValidator<Ate72Bytes, String> {

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext contexto) {
        return valor == null || valor.getBytes(StandardCharsets.UTF_8).length <= 72;
    }
}
