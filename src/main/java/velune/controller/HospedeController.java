package velune.controller;

import velune.dto.HospedeRequestDTO;
import velune.model.Hospede;
import velune.service.HospedeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
public class HotelController {

    @GetMapping("/hotel")
    public String hotel() {
        return "Velune funcionando!";
    }

    private final HospedeService service;

    public HotelController(HospedeService service) {
        this.service = service;
    }

    @GetMapping("/hospedes")
    public List<Hospede> buscarHospede() {
        return service.buscarTodos();
    }

    @PostMapping("/hospedes")
    public ResponseEntity<Hospede> salvarHospede(@Valid @RequestBody HospedeRequestDTO hospedeDTO) {

        Hospede hospede = service.salvarHospede(hospedeDTO);
        return ResponseEntity.created(URI.create("/hospedes/" + hospede.getDocumento())).body(hospede);
    }

    @GetMapping("/hospedes/{documento}")
    public ResponseEntity<Hospede> buscarPorDocumento(@PathVariable String documento) {

        return service.buscarPorDocumento(documento)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}