package velune.dto;

import velune.model.Cargo;

import java.util.List;

public record FuncionarioResponseDTO(String nome, Cargo cargo, List<EscalaResponseDTO> escalas) {
}
