package velune.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> tratarErroValidacao(MethodArgumentNotValidException ex) {

        Map<String, String> erros = new HashMap<>();
        var resultado = ex.getBindingResult();
        for (var erro : resultado.getFieldErrors()) {

            String campo = erro.getField();
            String mensagem = erro.getDefaultMessage();

            erros.put(campo, mensagem);
        }
        return ResponseEntity.badRequest().body(erros);
    }

    @ExceptionHandler(HospedeJaExisteException.class)
    public ResponseEntity<String> tratarErroHospedeExistente(HospedeJaExisteException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(HospedeNaoEncontradoException.class)
    public ResponseEntity<String> tratarErroHospedeNaoEncontrado(HospedeNaoEncontradoException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(QuartoNaoEncontradoException.class)
    public ResponseEntity<String> tratarErroQuartoNaoEncontrado(QuartoNaoEncontradoException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(QuartoIndisponivelException.class)
    public ResponseEntity<String> tratarQuartoIndisponivel(QuartoIndisponivelException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(ReservaNaoEncontradaException.class)
    public ResponseEntity<String> tratarReservaNaoEncontrada(ReservaNaoEncontradaException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(FuncionarioNaoEncontradoException.class)
    public ResponseEntity<String> tratarFuncionarioNaoEncontrado(FuncionarioNaoEncontradoException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(QuartoExistenteException.class)
    public ResponseEntity<String> tratarQuartoExistente(QuartoExistenteException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
