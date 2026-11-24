package clinica.model;

import java.time.LocalDate;

public abstract class Medico extends Usuario {
    protected String crm;
    protected double valorDaConsulta;
    protected String telefoneProfissional;
    // especialidade é derivada pela subclasse
    protected String especialidade;

    public Medico(int id, String nome, String sobrenome, String email, String senha,
                  LocalDate dataNascimento, char sexo, String crm,
                  double valorDaConsulta, String telefoneProfissional, String especialidade) {
        super(id, nome, sobrenome, email, senha, dataNascimento, sexo, telefoneProfissional);
        this.crm = crm;
        this.valorDaConsulta = valorDaConsulta;
        this.telefoneProfissional = telefoneProfissional;
        this.especialidade = especialidade;
    }

    /** Máximo de consultas por dia — pode ser sobrescrito por subclasse */
    public int maxConsultasDia() { return 3; }

    /** Valor base da consulta (pode ser sobrescrito para adicionar taxas) */
    public double getValorConsulta() { return valorDaConsulta; }

    public String getCrm()                    { return crm; }
    public double getValorDaConsulta()         { return valorDaConsulta; }
    public String getTelefoneProfissional()    { return telefoneProfissional; }
    public String getEspecialidade()           { return especialidade; }

    public void setCrm(String crm)                        { this.crm = crm; }
    public void setValorDaConsulta(double v)              { this.valorDaConsulta = v; }
    public void setTelefoneProfissional(String t)         { this.telefoneProfissional = t; }
    public void setEspecialidade(String especialidade)    { this.especialidade = especialidade; }

    /** Chamado pelo repositório após INSERT para atribuir o id gerado pelo banco. */
    public void setIdFromDb(int id)                       { this.id = id; }
}