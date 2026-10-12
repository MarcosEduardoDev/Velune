package velune.service;

import org.junit.jupiter.api.Test;
import velune.dto.QuartoRequestDTO;
import velune.exception.QuartoExistenteException;
import velune.exception.QuartoIndisponivelException;
import velune.model.TipoQuarto;
import velune.repository.QuartoRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class QuartoServiceTest {

    @Test
    void deveRecusarQuartoComNumeroDuplicado() {

        QuartoRepository repository = org.mockito.Mockito.mock(QuartoRepository.class);
        QuartoService service = new QuartoService(repository);

        QuartoRequestDTO quartoRequestDTO = new QuartoRequestDTO();
        quartoRequestDTO.setNumero(101);
        quartoRequestDTO.setTipo(TipoQuarto.SIMPLES);

        when(repository.existsByNumero(101)).thenReturn(true);

        assertThrows(QuartoExistenteException.class, () -> service.criarQuarto(quartoRequestDTO));
        verify(repository, never()).save(any());
    }
}
