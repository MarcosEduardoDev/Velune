package velune.repository;

import velune.model.Quarto;
import velune.model.TipoQuarto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuartoRepository extends JpaRepository<Quarto, Long> {

    Optional<Quarto> findByNumero (Integer numero);
    List<Quarto> findByTipo (TipoQuarto tipoQuarto);
    boolean existsByNumero(Integer numero);

}



