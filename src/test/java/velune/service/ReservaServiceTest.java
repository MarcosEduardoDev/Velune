package velune.service;

import org.junit.jupiter.api.Test;
import velune.exception.QuartoIndisponivelException;
import velune.exception.ReservaNaoEncontradaException;
import velune.model.Hospede;
import velune.model.Quarto;
import velune.model.Reserva;
import velune.model.TipoQuarto;
import velune.repository.HospedeRepository;
import velune.repository.QuartoRepository;
import velune.repository.ReservaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;


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

    @Test
    void deveLancarExcecaoSeNaoExistirReserva() {

        ReservaRepository reservaRepository = org.mockito.Mockito.mock(ReservaRepository.class);
        QuartoRepository quartoRepository = org.mockito.Mockito.mock(QuartoRepository.class);
        HospedeRepository hospedeRepository = org.mockito.Mockito.mock(HospedeRepository.class);

        ReservaService service = new ReservaService(
                hospedeRepository, reservaRepository, quartoRepository);

        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ReservaNaoEncontradaException.class, () -> service.novoCancelarReservaPorId(99L));

        verify(reservaRepository, never()).delete(any());
    }

    @Test
    void deveApagarReservaSeElaExistir() {

        ReservaRepository reservaRepository = org.mockito.Mockito.mock(ReservaRepository.class);
        QuartoRepository quartoRepository = org.mockito.Mockito.mock(QuartoRepository.class);
        HospedeRepository hospedeRepository = org.mockito.Mockito.mock(HospedeRepository.class);

        ReservaService service = new ReservaService(
                hospedeRepository, reservaRepository, quartoRepository);

        Quarto quarto = new Quarto(101, TipoQuarto.SIMPLES);
        Hospede hospede = new Hospede("Marcos", "123");
        Reserva reserva = new Reserva(hospede, quarto, LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 10), null);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        service.novoCancelarReservaPorId(1L);

        verify(reservaRepository).delete(reserva);
    }
}


