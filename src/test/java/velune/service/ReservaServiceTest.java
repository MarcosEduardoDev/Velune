package velune.service;

import org.junit.jupiter.api.Test;
import velune.exception.QuartoIndisponivelException;
import velune.model.Hospede;
import velune.model.Quarto;
import velune.model.Reserva;
import velune.model.TipoQuarto;
import velune.repository.HospedeRepository;
import velune.repository.QuartoRepository;
import velune.repository.ReservaRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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

        boolean disponivel = service.quartoDisponivel(quarto, LocalDate.of(2026, 12, 5),
                LocalDate.of(2026, 12, 12));

        assertFalse(disponivel);
    }

    @Test
    void naoDeveSalvarQuandoQuartoEstaIndisponivel() {

        ReservaRepository reservaRepository = org.mockito.Mockito.mock(ReservaRepository.class);
        QuartoRepository quartoRepository = org.mockito.Mockito.mock(QuartoRepository.class);
        HospedeRepository hospedeRepository = org.mockito.Mockito.mock(HospedeRepository.class);

        ReservaService service = new ReservaService(
                hospedeRepository, reservaRepository, quartoRepository);

        Quarto quarto = new Quarto(101, TipoQuarto.SIMPLES);
        Hospede hospede = new Hospede("Marcos", "123");
        Reserva reserva = new Reserva(hospede, quarto, LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 10), null);
        Reserva reserva1 = new Reserva(hospede, quarto, LocalDate.of(2026, 12, 5), LocalDate.of(2026, 12, 12), null);

        when(reservaRepository.findByQuarto(quarto)).thenReturn(List.of(reserva));

        boolean dispo = service.quartoDisponivel(quarto, reserva1.getDataCheckIn(), reserva1.getDataCheckOut());

        assertThrows(QuartoIndisponivelException.class, () -> service.criarReserva(reserva1));
        

    }
}


