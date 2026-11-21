package br.edu.unifor.missaomarte.servidor;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

class ProtecaoApiTest extends ApiTeste {

    @Autowired
    private JwtEncoder codificador;

    @Test
    void semTokenDa401ComProblemDetailEWwwAuthenticate() throws Exception {
        mvc.perform(get("/api/ranking"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", startsWith("Bearer")))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.detail").value("Token ausente, inválido ou expirado."));
    }

    @Test
    void tokenInvalidoDa401() throws Exception {
        mvc.perform(get("/api/ranking").header("Authorization", "Bearer isto-nao-e-um-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void tokenAssinadoPorUmFalsificadorDa401() throws Exception {
        byte[] outro = "chave-de-um-falsificador-com-mais-de-32-bytes".getBytes(StandardCharsets.UTF_8);
        JwtEncoder falsificador = NimbusJwtEncoder.withSecretKey(new SecretKeySpec(outro, "HmacSHA256"))
                .algorithm(MacAlgorithm.HS256).build();
        Instant agora = Instant.now();
        String token = assinar(falsificador, agora, agora.plusSeconds(3600));
        mvc.perform(get("/api/ranking").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenExpiradoDa401() throws Exception {
        // O decodificador tolera 60 s de diferença de relógio; aqui o token venceu há 5 min.
        Instant agora = Instant.now();
        String token = assinar(codificador, agora.minusSeconds(7500), agora.minusSeconds(300));
        mvc.perform(get("/api/ranking").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenValidoDa200() throws Exception {
        String token = tokenDe("sara");
        mvc.perform(get("/api/ranking").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void rotaQueNaoEstaNaListaPublicaExigeToken() throws Exception {
        mvc.perform(get("/api/usuarios")).andExpect(status().isUnauthorized());
        mvc.perform(get("/qualquer-coisa")).andExpect(status().isUnauthorized());
    }

    private static String assinar(JwtEncoder encoder, Instant emitido, Instant expira) {
        JwtClaimsSet claims = JwtClaimsSet.builder().subject("1").claim("nome", "teste")
                .issuedAt(emitido).expiresAt(expira).build();
        return encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();
    }
}
