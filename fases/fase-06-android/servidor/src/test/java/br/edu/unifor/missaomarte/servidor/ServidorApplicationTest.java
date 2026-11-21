package br.edu.unifor.missaomarte.servidor;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ServidorApplicationTest extends ApiTeste {

    @Test
    void contextoSobe() {
        assertNotNull(mvc);
    }
}
