package velune.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hospede_id")
    private Hospede hospede;

    @ManyToOne
    @JoinColumn(name = "quarto_id")
    private Quarto quarto;


    private LocalDate dataCheckIn;
    private LocalDate dataCheckOut;
    private String observacao;

    protected Reserva(){}

    public Long getId() {
        return id;
    }

    public Reserva(Hospede hospede, Quarto quarto, LocalDate dataCheckIn, LocalDate dataCheckOut, String observacao) {
        if (dataCheckOut.isBefore(dataCheckIn) || dataCheckOut.isEqual(dataCheckIn)) {
            throw new IllegalArgumentException("Data de check-out deve ser posterior à data de check-in.");
        }
        this.hospede = hospede;
        this.quarto = quarto;
        this.dataCheckIn = dataCheckIn;
        this.dataCheckOut = dataCheckOut;
        this.observacao = observacao;
    }

    public String getObservacao() {
        return observacao;
    }

    public Hospede getHospede() {
        return hospede;
    }

    public LocalDate getDataCheckOut() {
        return dataCheckOut;
    }

    public LocalDate getDataCheckIn() {
        return dataCheckIn;
    }

    public Quarto getQuarto() {
        return quarto;
    }

    @Override
    public String toString() {
        return "Reserva: " + "\n" +
                "Hóspede: " + hospede + "\n" +
                "Quarto: " + quarto + "\n" +
                "Data check-in: " + dataCheckIn + "\n" +
                "Data check-out: " + dataCheckOut + "\n" +
                "Observação: " + observacao;
    }

    public double calcularValor() {
        long dias = ChronoUnit.DAYS.between(dataCheckIn, dataCheckOut);
        return dias * quarto.getTipo().getPrecoDiaria();
    }

    public boolean estaAtiva(LocalDate data){
        return ((dataCheckIn.isBefore(data) ||
                dataCheckIn.isEqual(data)) &&
                dataCheckOut.isAfter(data));
    }






}
