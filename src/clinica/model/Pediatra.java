package clinica.model;

import java.time.LocalDate;

public class Pediatra extends Medico {

    private String faixaEtariaAtendida;
    private boolean atendeRecemNascidos;

    public Pediatra(int id, String nome, String sobrenome, String email, String senha,
                    LocalDate dataNascimento, char sexo, String crm,
                    double valorDaConsulta, String telefoneProfissional,
                    String faixaEtariaAtendida, boolean atendeRecemNascidos) {
        super(id, nome, sobrenome, email, senha, dataNascimento, sexo, crm,
              valorDaConsulta, telefoneProfissional, "Pediatra");
        this.faixaEtariaAtendida = faixaEtariaAtendida;
        this.atendeRecemNascidos = atendeRecemNascidos;
    }

    @Override
    public int maxConsultasDia() { return 2; }

    public String getFaixaEtariaAtendida()           { return faixaEtariaAtendida; }
    public boolean isAtendeRecemNascidos()            { return atendeRecemNascidos; }
    public void setFaixaEtariaAtendida(String f)     { this.faixaEtariaAtendida = f; }
    public void setAtendeRecemNascidos(boolean v)    { this.atendeRecemNascidos = v; }
}
