package velune.dto;

import velune.model.Turno;

import java.time.LocalDate;

public record EscalaResponseDTO(Turno turno, LocalDate data) {
}
