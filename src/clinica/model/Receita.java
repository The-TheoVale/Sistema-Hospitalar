package clinica.model;

import java.time.LocalDate;

/** Mapeia a tabela {@code receita} do banco. */
public class Receita {
    private int idReceita;
    private String prescricao;
    private String orientacao;
    private LocalDate dataValidade;
    private int idConsulta;

    public Receita(String prescricao, String orientacao, LocalDate dataValidade, int idConsulta) {
        this.prescricao = prescricao;
        this.orientacao = orientacao;
        this.dataValidade = dataValidade;
        this.idConsulta = idConsulta;
    }

    /** Construtor usado ao carregar do banco (já possui id). */
    public Receita(int idReceita, String prescricao, String orientacao,
                   LocalDate dataValidade, int idConsulta) {
        this.idReceita = idReceita;
        this.prescricao = prescricao;
        this.orientacao = orientacao;
        this.dataValidade = dataValidade;
        this.idConsulta = idConsulta;
    }

    public int getIdReceita()           { return idReceita; }
    public void setIdReceita(int id)    { this.idReceita = id; }
    public String getPrescricao()       { return prescricao; }
    public String getOrientacao()       { return orientacao; }
    public LocalDate getDataValidade()  { return dataValidade; }
    public int getIdConsulta()          { return idConsulta; }

    public void setPrescricao(String p)         { this.prescricao = p; }
    public void setOrientacao(String o)         { this.orientacao = o; }
    public void setDataValidade(LocalDate d)    { this.dataValidade = d; }
}
