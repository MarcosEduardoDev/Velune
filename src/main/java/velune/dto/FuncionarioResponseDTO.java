package velune.dto;

import velune.model.Cargo;
import velune.model.Escala;

import java.util.List;

public record FuncionarioResponseDTO(String nome, Cargo cargo) {
}
