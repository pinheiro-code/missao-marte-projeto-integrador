package br.edu.unifor.missaomarte.servidor.seguranca;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import br.edu.unifor.missaomarte.servidor.dto.TokenResponse;

@Service
public class TokenService {

    private static final Duration VALIDADE = Duration.ofHours(2);

    private final JwtEncoder codificador;
    private final Clock relogio;

    public TokenService(JwtEncoder codificador, Clock relogio) {
        this.codificador = codificador;
        this.relogio = relogio;
    }

    // Só o id (sub) e o nome vão no token; a senha nunca.
    public TokenResponse emitir(Long usuarioId, String nome) {
        Instant agora = relogio.instant().truncatedTo(ChronoUnit.SECONDS);
        Instant expira = agora.plus(VALIDADE);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(String.valueOf(usuarioId))
                .claim("nome", nome)
                .issuedAt(agora)
                .expiresAt(expira)
                .build();
        JwsHeader cabecalho = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = codificador.encode(JwtEncoderParameters.from(cabecalho, claims)).getTokenValue();
        return new TokenResponse(token, "Bearer", LocalDateTime.ofInstant(expira, relogio.getZone()));
    }
}
