package clinica.model;

import java.time.LocalDate;

public class Cardiologista extends Medico {

    private static final double ADICIONAL = 150;

    private boolean realizaEcocardiograma;

    public Cardiologista(int id, String nome, String sobrenome, String email, String senha,
                         LocalDate dataNascimento, char sexo, String crm,
                         double valorDaConsulta, String telefoneProfissional,
                         boolean realizaEcocardiograma) {
        super(id, nome, sobrenome, email, senha, dataNascimento, sexo, crm,
              valorDaConsulta, telefoneProfissional, "Cardiologista");
        this.realizaEcocardiograma = realizaEcocardiograma;
    }

    @Override
    public double getValorConsulta() { return super.getValorConsulta() + ADICIONAL; }

    public boolean isRealizaEcocardiograma()              { return realizaEcocardiograma; }
    public void setRealizaEcocardiograma(boolean v)       { this.realizaEcocardiograma = v; }
}
