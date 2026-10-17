package velune.service;

import org.junit.jupiter.api.Test;
import velune.dto.FuncionarioResponseDTO;
import velune.exception.FuncionarioNaoEncontradoException;
import velune.model.Cargo;
import velune.model.Escala;
import velune.model.Funcionario;
import velune.model.Turno;
import velune.repository.FuncionarioRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FuncionarioServiceTest {

    @Test
    void deveLancarExcecaoQuandoFuncionarioNaoExiste() {

        FuncionarioRepository repository = org.mockito.Mockito.mock(FuncionarioRepository.class);
        FuncionarioService service = new FuncionarioService(repository);

        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(FuncionarioNaoEncontradoException.class, () -> service.buscarPorId(99L));
    }

    @Test
    void deveAdicionarEscalaENotificacaoAoDefinirEscala() {

        FuncionarioRepository repository = org.mockito.Mockito.mock(FuncionarioRepository.class);
        FuncionarioService service = new FuncionarioService(repository);

            Funcionario funcionario = new Funcionario("Ana", "10/05/2026", 26, Cargo.GERENTE);
            when(repository.findById(1L)).thenReturn(Optional.of(funcionario));
            when(repository.save(funcionario)).thenReturn(funcionario);

            service.definirEscala(1L, Turno.MANHA, LocalDate.of(2026, 12, 10));

            assertEquals(1, funcionario.listarEscalas().size());
            assertEquals(1, funcionario.listarNotificacoes().size());

            verify(repository).save(funcionario);
    }

    @Test
    void deveMontarCartaoComNomeCargoEEscalas() {

        FuncionarioRepository repository = org.mockito.Mockito.mock(FuncionarioRepository.class);
        FuncionarioService service = new FuncionarioService(repository);

        Funcionario funcionario = new Funcionario("Roberta", "05/05/2026", 26, Cargo.DIRETOR);
        funcionario.adicionarEscala(new Escala(Turno.MANHA, LocalDate.of(2026, 5, 10)));

        when(repository.findById(1L)).thenReturn(Optional.of(funcionario));

        FuncionarioResponseDTO func = service.escalaFuncionario(1L);

        assertEquals("Roberta", func.nome());
        assertEquals(Cargo.DIRETOR, func.cargo());
        assertEquals(1, func.escalas().size());
        assertEquals(Turno.MANHA, func.escalas().get(0).turno());
        assertEquals(LocalDate.of(2026, 5, 10), func.escalas().get(0).data());

    }




}
