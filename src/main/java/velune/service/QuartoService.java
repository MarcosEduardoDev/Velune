package velune.service;

import velune.dto.QuartoRequestDTO;
import velune.exception.QuartoExistenteException;
import velune.model.Quarto;
import velune.repository.QuartoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuartoService {

    private final QuartoRepository repository;

    public QuartoService(QuartoRepository repository) {
        this.repository = repository;
    }

    public Quarto criarQuarto(QuartoRequestDTO quartoRequestDTO){

        if (repository.existsByNumero(quartoRequestDTO.getNumero())){
            throw new QuartoExistenteException("Já existe um quarto com esse número.");
        }
        Quarto quarto = new Quarto(quartoRequestDTO.getNumero(), quartoRequestDTO.getTipo());
        return repository.save(quarto);
    }

    public List<Quarto> listarQuartos(){
        return repository.findAll();
    }

}
