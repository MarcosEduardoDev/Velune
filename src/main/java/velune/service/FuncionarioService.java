package velune.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import velune.dto.EscalaResponseDTO;
import velune.dto.FuncionarioRequestDTO;
import velune.dto.FuncionarioResponseDTO;
import velune.exception.FuncionarioNaoEncontradoException;
import velune.model.Escala;
import velune.model.Funcionario;
import velune.model.Notificacao;
import velune.model.Turno;
import velune.repository.FuncionarioRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FuncionarioService {

    private final FuncionarioRepository repository;

    public FuncionarioService(FuncionarioRepository repository) {
        this.repository = repository;
    }

    public Funcionario salvarFuncionario(FuncionarioRequestDTO funcionarioDTO){

        Funcionario funcionario = new Funcionario(funcionarioDTO.getNome(),
                funcionarioDTO.getDataDeEntrada(),
                funcionarioDTO.getIdade(),
                funcionarioDTO.getCargo());

        return repository.save(funcionario);
    }

    public List<Funcionario> listarFuncionarios() {

        return repository.findAll();
    }

    public Funcionario buscarPorId(Long funcionarioId){

        return repository.findById(funcionarioId)
                .orElseThrow(() -> new FuncionarioNaoEncontradoException("Funcionário não encontrado."));
    }

    @Transactional(readOnly = true)
    public Funcionario definirEscala(Long funcionarioId, Turno turno, LocalDate data){

        Funcionario funcionario = buscarPorId(funcionarioId);
        funcionario.adicionarEscala(new Escala(turno, data));
        funcionario.adicionarNotificacao(new Notificacao("Você foi escalado para "
                + turno + " no dia " + data, LocalDateTime.now()));

        return repository.save(funcionario);
    }

    @Transactional(readOnly = true)
    public FuncionarioResponseDTO escalaFuncionario(Long funcionarioId){

        Funcionario funcionario = buscarPorId(funcionarioId);
        List<EscalaResponseDTO> escalas = funcionario.listarEscalas().stream()
                .map(e -> new EscalaResponseDTO(e.getTurno(), e.getData()))
                .toList();

        return new FuncionarioResponseDTO(funcionario.getNome(), funcionario.getCargo(), escalas);
    }




}
