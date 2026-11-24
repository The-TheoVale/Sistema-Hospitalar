package clinica.model;

import java.time.LocalDateTime;

public class Consulta {
    private int idConsulta;
    private Medico medico;
    private Paciente paciente;
    private LocalDateTime dataHora;
    private StatusConsulta status;
    private String descricao;

    /** Construtor para nova consulta (ainda não persistida). */
    public Consulta(Medico medico, Paciente paciente, LocalDateTime dataHora) {
        this(0, medico, paciente, dataHora, StatusConsulta.AGENDADA, null);
    }

    /** Construtor usado ao carregar do banco. */
    public Consulta(int idConsulta, Medico medico, Paciente paciente,
                    LocalDateTime dataHora, StatusConsulta status, String descricao) {
        this.idConsulta = idConsulta;
        this.medico = medico;
        this.paciente = paciente;
        this.dataHora = dataHora;
        this.status = status;
        this.descricao = descricao;
    }

    /** Marca a consulta como realizada e registra a descrição do atendimento. */
    public void realizar(String descricao) {
        this.descricao = descricao;
        this.status = StatusConsulta.REALIZADA;
    }

    public int getIdConsulta()          { return idConsulta; }
    public void setIdConsulta(int id)   { this.idConsulta = id; }

    public Medico getMedico()           { return medico; }
    public Paciente getPaciente()       { return paciente; }
    public LocalDateTime getDataHora()  { return dataHora; }
    public StatusConsulta getStatus()   { return status; }
    public String getDescricao()        { return descricao; }

    public void setStatus(StatusConsulta status)   { this.status = status; }
    public void setDescricao(String descricao)     { this.descricao = descricao; }
}
