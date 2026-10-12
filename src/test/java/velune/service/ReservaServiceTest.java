package velune.service;

import org.junit.jupiter.api.Test;
import org.springframework.cglib.core.Local;
import velune.model.Hospede;
import velune.model.Quarto;
import velune.model.Reserva;
import velune.model.TipoQuarto;
import velune.repository.HospedeRepository;
import velune.repository.QuartoRepository;
import velune.repository.ReservaRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


public class ReservaServiceTest {

    @Test
    void deveAceitarReservaQueComecaNoDiaDoCheckOutAnterior() {

        ReservaRepository reservaRepository = org.mockito.Mockito.mock(ReservaRepository.class);
        QuartoRepository quartoRepository = org.mockito.Mockito.mock(QuartoRepository.class);
        HospedeRepository hospedeRepository = org.mockito.Mockito.mock(HospedeRepository.class);

        ReservaService service = new ReservaService(hospedeRepository, reservaRepository, quartoRepository);

        Hospede hospede = new Hospede("Marcos", "123");
        Quarto quarto = new Quarto(101, TipoQuarto.SIMPLES);
        Reserva reserva = new Reserva(hospede, quarto, LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 10), null);

        when(reservaRepository.findByQuarto(quarto)).thenReturn(List.of(reserva));

        boolean disponivel = service.quartoDisponivel(quarto, LocalDate.of(2026, 12, 10),
                LocalDate.of(2026, 12, 15));

        assertTrue(disponivel);
    }

    @Test
    void deveFicarIndisponivelQuandoCruzarDatas() {

        ReservaRepository reservaRepository = org.mockito.Mockito.mock(ReservaRepository.class);
        QuartoRepository quartoRepository = org.mockito.Mockito.mock(QuartoRepository.class);
        HospedeRepository hospedeRepository = org.mockito.Mockito.mock(HospedeRepository.class);

        ReservaService service = new ReservaService(hospedeRepository, reservaRepository, quartoRepository);

        Hospede hospede = new Hospede("Marcos", "123");
        Quarto quarto = new Quarto(101, TipoQuarto.SIMPLES);
        Reserva reserva = new Reserva(hospede, quarto, LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 10), null);

        when(reservaRepository.findByQuarto(quarto)).thenReturn(List.of(reserva));

        boolean disponivel = service.quartoDisponivel(quarto, LocalDate.of(2026, 12, 05),
                LocalDate.of(2026, 12, 12));

        assertFalse(disponivel);
    }
}


