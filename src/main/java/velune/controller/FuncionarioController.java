package velune.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import velune.dto.EscalaRequestDTO;
import velune.dto.FuncionarioRequestDTO;
import velune.model.Escala;
import velune.model.Funcionario;
import velune.service.FuncionarioService;

import java.util.List;

@RestController
public class FuncionarioController {

    private final FuncionarioService service;

    public FuncionarioController(FuncionarioService service) {
        this.service = service;
    }

    @GetMapping("/funcionarios")
    public List<Funcionario> listarFuncionarios() {

        return service.listarFuncionarios();
    }

    @PostMapping("/funcionarios")
    public ResponseEntity<Funcionario> criarFuncionario(@Valid @RequestBody FuncionarioRequestDTO funcionarioRequestDTO) {

        Funcionario funcionario = service.salvarFuncionario(funcionarioRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(funcionario);
    }

    @GetMapping("/funcionarios/{id}")
    public Funcionario buscarFuncionarioPorId(@PathVariable Long id) {

        return service.buscarPorId(id);
    }

    @PostMapping("/funcionarios/{id}/escalas")
    public ResponseEntity<Funcionario> definirEscala(@PathVariable Long id, @Valid @RequestBody
                                                     EscalaRequestDTO escalaDTO) {

        Funcionario funcionario = service.definirEscala(id, escalaDTO.getTurno(), escalaDTO.getData());
        return ResponseEntity.status(HttpStatus.CREATED).body(funcionario);
    }

    @GetMapping("/funcionarios/{id}/escalas")
    public List<Escala> listarEscalas(@PathVariable Long id) {

        return service.listarEscalasPorId(id);
    }

}

