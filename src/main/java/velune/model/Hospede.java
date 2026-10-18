package velune.model;

import jakarta.persistence.*;

@Entity
public class Hospede {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String documento;

    protected Hospede() {
    }

    public Hospede(String nome, String documento) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser vazio.");
        }
        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException("Documento não pode ser vazio.");
        }
        this.nome = nome;
        this.documento = documento;
    }

    public String getNome() {
        return nome;
    }
    public Long getId() { return id;}
    public String getDocumento() {
        return documento;
    }

    @Override
    public String toString() {
        return "Hospede: " + "\n" +
                "Nome: " + nome + "\n" +
                "Documento: " + documento;
    }
}
