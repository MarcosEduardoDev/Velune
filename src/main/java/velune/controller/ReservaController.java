package velune.controller;

import velune.dto.ReservaRequestDTO;
import velune.model.Reserva;
import velune.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class ReservaController {

    private final ReservaService service;

    public ReservaController(ReservaService service) {
        this.service = service;
    }

    @GetMapping("/reservas")
    public List<Reserva> listarReservas(){
        return service.listarReservas();
    }

    @PostMapping("/reservas")
    public ResponseEntity<Reserva> criarReserva(@Valid @RequestBody ReservaRequestDTO reservaDTO){

        Reserva reserva = service.criarReservaPorId(reservaDTO.getHospedeId(),
                reservaDTO.getQuartoId(),
                reservaDTO.getDataCheckIn(),
                reservaDTO.getDataCheckOut(),
                reservaDTO.getObservacao());

        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/reservas/{quartoId}/{data}")
    public void deletarReserva(@PathVariable Long quartoId, @PathVariable  LocalDate data){

        service.cancelarReservaPorId(quartoId, data);
    }

    @GetMapping("/reservas/{data}")
    public List<Reserva> buscarReservasPorData(@PathVariable LocalDate data){

       return service.buscarReservasNaData(data);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/reservas/{reservaId}")
    public void deletarReservaPorId(@PathVariable Long reservaId){

        service.novoCancelarReservaPorId(reservaId);
    }





}
