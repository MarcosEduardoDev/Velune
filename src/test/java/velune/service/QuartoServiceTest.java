package velune.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import velune.dto.QuartoRequestDTO;
import velune.exception.QuartoExistenteException;
import velune.model.Quarto;
import velune.model.TipoQuarto;
import velune.repository.QuartoRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void deveAceitarNovoQuarto() {

        QuartoRepository repository = org.mockito.Mockito.mock(QuartoRepository.class);
        QuartoService service = new QuartoService(repository);

        QuartoRequestDTO quartoRequestDTO = new QuartoRequestDTO();
        quartoRequestDTO.setNumero(101);
        quartoRequestDTO.setTipo(TipoQuarto.SIMPLES);

        when(repository.existsByNumero(101)).thenReturn(false);

        ArgumentCaptor<Quarto> captor = ArgumentCaptor.forClass(Quarto.class);

        service.criarQuarto(quartoRequestDTO);

        verify(repository).save(captor.capture());
        Quarto quartoCapturado = captor.getValue();

        assertEquals(101, quartoCapturado.getNumero());
        assertEquals(TipoQuarto.SIMPLES, quartoCapturado.getTipo());
    }
}
