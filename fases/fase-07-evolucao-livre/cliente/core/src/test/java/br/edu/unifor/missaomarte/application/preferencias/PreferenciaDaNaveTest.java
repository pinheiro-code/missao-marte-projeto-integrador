package br.edu.unifor.missaomarte.application.preferencias;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.unifor.missaomarte.application.servidor.ArmazemDeTexto;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Com um armazém em memória, no lugar das Preferences do jogo. */
class PreferenciaDaNaveTest {

    private final ArmazemEmMemoria armazem = new ArmazemEmMemoria();
    private final PreferenciaDaNave preferencia = new PreferenciaDaNave(armazem);

    @Test
    void semValorGravadoValeAPrimeiraCor() {
        assertEquals(CorDaNave.ORIGINAL, preferencia.atual());
    }

    @Test
    void salvaELe() {
        preferencia.salvar(CorDaNave.AZUL);

        assertEquals(CorDaNave.AZUL, preferencia.atual());
        assertEquals(CorDaNave.AZUL, new PreferenciaDaNave(armazem).atual(), "volta depois de fechar e abrir");
    }

    @Test
    void valorInvalidoVoltaAPrimeiraCor() {
        for (String invalido : new String[] {"", "  ", "roxa", "azul", "AZUL ", "Azul", "7"}) {
            armazem.gravar(PreferenciaDaNave.CHAVE, invalido);
            assertEquals(CorDaNave.ORIGINAL, preferencia.atual(), "valor: [" + invalido + "]");
        }
    }

    @Test
    void gravaSoAChaveDaCor() {
        preferencia.salvar(CorDaNave.VERDE);
        preferencia.salvar(CorDaNave.ROSA);

        assertEquals(Set.of("nave.cor"), armazem.valores.keySet());
        assertEquals("ROSA", armazem.valores.get("nave.cor"));
    }

    @Test
    void naoMexeNasChavesDoServidor() {
        armazem.gravar("servidor.endereco", "192.168.0.10");

        preferencia.salvar(CorDaNave.AMARELA);

        assertEquals("192.168.0.10", armazem.valores.get("servidor.endereco"));
        assertEquals(2, armazem.valores.size());
    }

    private static final class ArmazemEmMemoria implements ArmazemDeTexto {

        private final Map<String, String> valores = new HashMap<>();

        @Override
        public String ler(String chave) {
            return valores.get(chave);
        }

        @Override
        public void gravar(String chave, String valor) {
            valores.put(chave, valor);
        }

        @Override
        public void remover(String chave) {
            valores.remove(chave);
        }
    }
}
