package br.edu.unifor.missaomarte.servidor.seguranca;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import tools.jackson.databind.json.JsonMapper;

@Configuration
public class SegurancaConfig {

    private static final Logger log = LoggerFactory.getLogger(SegurancaConfig.class);

    @Bean
    SecurityFilterChain filtro(HttpSecurity http, AuthenticationEntryPoint naoAutorizado) throws Exception {
        // Sem sessão e sem cookie: o token vai no cabeçalho, então não há o que o CSRF proteger.
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(sessao -> sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(rotas -> rotas
                        .requestMatchers(rotasPublicas()).permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(servidor -> servidor
                        .jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(naoAutorizado))
                .exceptionHandling(erros -> erros.authenticationEntryPoint(naoAutorizado));
        return http.build();
    }

    // Tudo exige token, menos o que está aqui.
    static RequestMatcher[] rotasPublicas() {
        PathPatternRequestMatcher.Builder rota = PathPatternRequestMatcher.withDefaults();
        return new RequestMatcher[] {
                rota.matcher(HttpMethod.POST, "/api/usuarios"),
                rota.matcher(HttpMethod.POST, "/api/auth/login"),
                rota.matcher("/error")
        };
    }

    // O padrão do Bearer responde 401 com corpo vazio; aqui ele mantém o WWW-Authenticate e ganha um ProblemDetail.
    @Bean
    AuthenticationEntryPoint naoAutorizado(JsonMapper json) {
        BearerTokenAuthenticationEntryPoint bearer = new BearerTokenAuthenticationEntryPoint();
        return (requisicao, resposta, excecao) -> {
            bearer.commence(requisicao, resposta, excecao);
            ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED,
                    "Token ausente, inválido ou expirado.");
            problema.setTitle("Não autorizado");
            resposta.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
            resposta.setCharacterEncoding(StandardCharsets.UTF_8.name());
            json.writeValue(resposta.getOutputStream(), problema);
        };
    }

    @Bean
    PasswordEncoder codificadorDeSenha() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    SecretKey chaveDoToken(@Value("${missaomarte.jwt.segredo:}") String segredo) {
        byte[] bytes;
        if (segredo.isBlank()) {
            bytes = new byte[32];
            new SecureRandom().nextBytes(bytes);
            log.warn("MISSAOMARTE_JWT_SEGREDO não definida: usando um segredo aleatório. "
                    + "Os tokens deixam de valer quando o servidor reinicia.");
        } else {
            bytes = segredo.getBytes(StandardCharsets.UTF_8);
            if (bytes.length < 32) {
                throw new IllegalStateException("MISSAOMARTE_JWT_SEGREDO precisa ter pelo menos 32 bytes.");
            }
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    // O resource server só confia em RS256 por padrão; HS256 precisa da chave e do algoritmo declarados.
    @Bean
    JwtEncoder jwtEncoder(SecretKey chave) {
        return NimbusJwtEncoder.withSecretKey(chave).algorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    JwtDecoder jwtDecoder(SecretKey chave) {
        return NimbusJwtDecoder.withSecretKey(chave).macAlgorithm(MacAlgorithm.HS256).build();
    }
}
