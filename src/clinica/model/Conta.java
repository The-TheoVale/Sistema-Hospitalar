package clinica.model;

import java.time.LocalDate;

public class Conta {
    private int idConta;
    private Paciente paciente;
    private int idConsulta;
    private double valorCobrado;
    private LocalDate dataDeVencimento;
    private StatusPagamento situacao;

    public Conta(Paciente paciente, int idConsulta, double valorCobrado, LocalDate dataDeVencimento) {
        this.paciente = paciente;
        this.idConsulta = idConsulta;
        this.valorCobrado = valorCobrado;
        this.dataDeVencimento = dataDeVencimento;
        this.situacao = StatusPagamento.PENDENTE;
    }

    /** Construtor usado ao carregar do banco. */
    public Conta(int idConta, Paciente paciente, int idConsulta,
                 double valorCobrado, LocalDate dataDeVencimento, StatusPagamento situacao) {
        this.idConta = idConta;
        this.paciente = paciente;
        this.idConsulta = idConsulta;
        this.valorCobrado = valorCobrado;
        this.dataDeVencimento = dataDeVencimento;
        this.situacao = situacao;
    }

    public void pagar() {
        if (situacao == StatusPagamento.PENDENTE) situacao = StatusPagamento.PAGA;
    }

    public int getIdConta()                 { return idConta; }
    public void setIdConta(int id)          { this.idConta = id; }
    public Paciente getPaciente()           { return paciente; }
    public int getIdConsulta()              { return idConsulta; }
    public double getValorCobrado()         { return valorCobrado; }
    public LocalDate getDataDeVencimento()  { return dataDeVencimento; }
    public StatusPagamento getSituacao()    { return situacao; }
}
