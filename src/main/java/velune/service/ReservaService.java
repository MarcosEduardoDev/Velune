package velune.service;

import velune.exception.*;
import velune.model.Hospede;
import velune.model.Quarto;
import velune.model.Reserva;
import velune.repository.HospedeRepository;
import velune.repository.QuartoRepository;
import velune.repository.ReservaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservaService {

    private final HospedeRepository hospedeRepository;
    private final ReservaRepository reservaRepository;
    private final QuartoRepository quartoRepository;

    public ReservaService(HospedeRepository hospedeRepository, ReservaRepository repository, QuartoRepository quartoRepository) {
        this.hospedeRepository = hospedeRepository;
        this.reservaRepository = repository;
        this.quartoRepository = quartoRepository;
    }

    public boolean quartoDisponivel(Quarto quarto, LocalDate dataCheckIn, LocalDate dataCheckOut) {
        boolean temConflito = reservaRepository.findByQuarto(quarto).stream()
                .anyMatch(r -> dataCheckIn.isBefore(r.getDataCheckOut()) && dataCheckOut.isAfter(r.getDataCheckIn()));
        return !temConflito;
    }

    public Reserva criarReserva(Reserva reserva) {
        if (quartoDisponivel(reserva.getQuarto(), reserva.getDataCheckIn(), reserva.getDataCheckOut())) {
            return reservaRepository.save(reserva);
        } else {
            throw new QuartoIndisponivelException("Quarto indisponível nesta data para reserva.");
        }
    }

    public List<Reserva> listarReservas() {
        return reservaRepository.findAll();
    }

    public void cancelarReserva(Quarto quarto, LocalDate data){
        Reserva reserva = reservaRepository.findByQuarto(quarto).stream()
                .filter(r -> r.estaAtiva(data))
                .findFirst()
                .orElseThrow(() -> new ReservaNaoEncontradaException("Reserva não encontrada."));

        reservaRepository.delete(reserva);
    }

    public Reserva criarReservaPorId(Long hospedeId, Long quartoId, LocalDate dataCheckIn,
                                     LocalDate dataCheckOut, String observacao){

        Hospede hospede = hospedeRepository.findById(hospedeId)
                .orElseThrow(() -> new HospedeNaoEncontradoException("Hóspede não encontrado."));

        Quarto quarto = quartoRepository.findById(quartoId)
                .orElseThrow(() -> new QuartoNaoEncontradoException("Número de quarto não encontrado."));

        return criarReserva(new Reserva(hospede, quarto, dataCheckIn, dataCheckOut, observacao));
    }

    public void cancelarReservaPorId(Long quartoId, LocalDate data){

        Quarto quarto = quartoRepository.findById(quartoId)
                .orElseThrow(() -> new QuartoNaoEncontradoException("Quarto não encontrado."));

        cancelarReserva(quarto, data);
    }

    public List<Reserva> buscarReservasNaData(LocalDate data){

       return reservaRepository.findAll().stream()
               .filter(r -> r.estaAtiva(data))
               .toList();
    }

    public void novoCancelarReservaPorId(Long reservaId){

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ReservaNaoEncontradaException("Reserva não encontrada."));

        reservaRepository.delete(reserva);
    }
}
