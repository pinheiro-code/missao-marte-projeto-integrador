package br.edu.unifor.missaomarte.servidor.erro;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import tools.jackson.core.JacksonException;

// Todo erro sai como ProblemDetail (RFC 9457), sem mensagem interna nem stack trace.
@RestControllerAdvice
public class TratadorDeErros extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(TratadorDeErros.class);

    record ErroDeCampo(String campo, String mensagem) {
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<ErroDeCampo> erros = new ArrayList<>();
        for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
            erros.add(new ErroDeCampo(erro.getField(), erro.getDefaultMessage()));
        }
        return invalido(erros);
    }

    // @Min/@Max nos parâmetros de consulta (pagina, tamanho) chegam por aqui.
    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<ErroDeCampo> erros = new ArrayList<>();
        ex.getParameterValidationResults().forEach(resultado -> resultado.getResolvableErrors()
                .forEach(erro -> erros.add(new ErroDeCampo(
                        resultado.getMethodParameter().getParameterName(), erro.getDefaultMessage()))));
        return invalido(erros);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        return invalido(List.of(new ErroDeCampo(ex.getPropertyName(), "Valor inválido.")));
    }

    // Enum desconhecido ou tipo errado num campo: o nome do campo sai do caminho da exceção do Jackson.
    // JSON quebrado: só um texto genérico.
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (ex.getCause() instanceof JacksonException jackson && !jackson.getPath().isEmpty()) {
            String campo = jackson.getPath().get(jackson.getPath().size() - 1).getPropertyName();
            if (campo != null) {
                return invalido(List.of(new ErroDeCampo(campo, "Valor inválido.")));
            }
        }
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "O corpo da requisição não é um JSON válido.");
        problema.setTitle("Requisição inválida");
        return ResponseEntity.badRequest().body(problema);
    }

    // Corrida entre dois cadastros com o mesmo nome: o índice único do banco barra o segundo.
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<Object> conflito(DataIntegrityViolationException ex) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "Já existe um usuário com esse nome.");
        problema.setTitle("Conflito");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problema);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<Object> inesperado(Exception ex) {
        log.error("Erro inesperado", ex);
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno no servidor.");
        problema.setTitle("Erro interno");
        return ResponseEntity.internalServerError().body(problema);
    }

    private static ResponseEntity<Object> invalido(List<ErroDeCampo> erros) {
        List<ErroDeCampo> ordenados = new ArrayList<>(erros);
        ordenados.sort(Comparator.comparing(ErroDeCampo::campo).thenComparing(ErroDeCampo::mensagem));
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Um ou mais campos são inválidos.");
        problema.setTitle("Requisição inválida");
        problema.setProperty("erros", ordenados);
        return ResponseEntity.badRequest().body(problema);
    }
}
