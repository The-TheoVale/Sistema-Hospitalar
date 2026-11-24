package clinica.model;

import java.time.LocalDate;

public class ClinicoGeral extends Medico {

    public ClinicoGeral(int id, String nome, String sobrenome, String email, String senha,
                        LocalDate dataNascimento, char sexo, String crm,
                        double valorDaConsulta, String telefoneProfissional) {
        super(id, nome, sobrenome, email, senha, dataNascimento, sexo, crm,
              valorDaConsulta, telefoneProfissional, "Clínico Geral");
    }
}
