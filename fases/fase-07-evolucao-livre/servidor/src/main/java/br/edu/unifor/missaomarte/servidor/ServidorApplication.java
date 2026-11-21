package br.edu.unifor.missaomarte.servidor;

import java.time.Clock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ServidorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServidorApplication.class, args);
    }

    // Relógio injetado: os testes podem fixar a hora sem mexer nas regras.
    @Bean
    Clock relogio() {
        return Clock.systemDefaultZone();
    }
}
