package clinica.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Paciente extends Usuario {
    private String cpf;
    private String endereco;
    private List<Consulta> historico;

    public Paciente(int id, String nome, String sobrenome, String email, String senha,
                    LocalDate dataNascimento, char sexo, String cpf, String endereco, String telefone) {
        super(id, nome, sobrenome, email, senha, dataNascimento, sexo, telefone);
        this.cpf = cpf;
        this.endereco = endereco;
        this.historico = new ArrayList<>();
    }

    public void adicionarAoHistorico(Consulta consulta) {
        historico.add(consulta);
    }

    public String getCpf()           { return cpf; }
    public String getEndereco()      { return endereco; }
    public List<Consulta> getHistorico() { return historico; }

    public void setCpf(String cpf)           { this.cpf = cpf; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    /** Chamado pelo repositório após INSERT para atribuir o id gerado pelo banco. */
    public void setIdFromDb(int id)          { this.id = id; }
}