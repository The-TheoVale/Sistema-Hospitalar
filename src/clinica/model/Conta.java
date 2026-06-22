package clinica.model;

public class Conta {
    Paciente paciente;
    double valor;
    StatusPagamento situacao;

    public Conta(Paciente paciente, double valor) {
        this.paciente = paciente;
        this.valor = valor;
        this.situacao = StatusPagamento.PENDENTE;
    }

    public void pagar(){
        if (situacao == StatusPagamento.PENDENTE) situacao = StatusPagamento.PAGA;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public double getValor() {
        return valor;
    }

    public StatusPagamento getSituacao() {
        return situacao;
    }

    
}
