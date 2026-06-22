package clinica.model;

import java.time.LocalDate;

public class Consulta {
    private Medico medico;
    private Paciente paciente;
    private LocalDate data;
    private String descricao;
    private String receita;
    private String exames;
    private StatusConsulta status;
    private double valorPago;
    public Consulta(Medico medico, Paciente paciente, LocalDate data) {
        this.medico = medico;
        this.paciente = paciente;
        this.data = data;
        status = StatusConsulta.AGENDADA;
        valorPago = -1;
    }

    public void realizar (String descricao, String receita, String exames){
        this.descricao = descricao;
        this.receita = receita;
        this.exames = exames;

        if (paciente.temPlano() && medico.aceitaPlano(paciente.getPlanoSaude())){
            this.valorPago = 0;
        } else {
            this.valorPago = medico.getValorConsulta();
        }

        this.status = StatusConsulta.REALIZADA;
    }


    public void setStatus(StatusConsulta status) {
        this.status = status;
    }


    public String getIdentificador(){
        return medico.getId() + "_" + paciente.getId() + "_" + this.data;
    }
    public Medico getMedico() {
        return medico;
    }
    public Paciente getPaciente() {
        return paciente;
    }
    public LocalDate getData() {
        return data;
    }
    public StatusConsulta getStatus() {
        return status;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getReceita() {
        return receita;
    }

    public String getExames() {
        return exames;
    }

    public double getValorPago() {
        return valorPago;
    }

    public void setValorPago(double valorPago) {
        this.valorPago = valorPago;
    }

    
    
}
