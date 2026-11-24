package clinica.model;

import java.time.LocalDate;

/** Nova especialidade — mapeia a tabela {@code cirurgiao} do banco. */
public class Cirurgiao extends Medico {

    private String areaCirurgica;
    private short tempoExperiencia; // em anos

    public Cirurgiao(int id, String nome, String sobrenome, String email, String senha,
                     LocalDate dataNascimento, char sexo, String crm,
                     double valorDaConsulta, String telefoneProfissional,
                     String areaCirurgica, short tempoExperiencia) {
        super(id, nome, sobrenome, email, senha, dataNascimento, sexo, crm,
              valorDaConsulta, telefoneProfissional, "Cirurgião");
        this.areaCirurgica = areaCirurgica;
        this.tempoExperiencia = tempoExperiencia;
    }

    public String getAreaCirurgica()           { return areaCirurgica; }
    public short getTempoExperiencia()          { return tempoExperiencia; }
    public void setAreaCirurgica(String a)     { this.areaCirurgica = a; }
    public void setTempoExperiencia(short t)   { this.tempoExperiencia = t; }
}
