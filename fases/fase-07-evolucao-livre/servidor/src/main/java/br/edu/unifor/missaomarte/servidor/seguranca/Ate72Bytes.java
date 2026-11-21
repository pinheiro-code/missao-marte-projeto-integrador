package br.edu.unifor.missaomarte.servidor.seguranca;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.RECORD_COMPONENT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

// O BCrypt só lê os 72 primeiros bytes e corta o resto sem avisar; acento ocupa 2 bytes em UTF-8.
@Target({ FIELD, PARAMETER, RECORD_COMPONENT })
@Retention(RUNTIME)
@Constraint(validatedBy = Ate72BytesValidator.class)
public @interface Ate72Bytes {

    String message() default "A senha deve ter no máximo 72 bytes.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
