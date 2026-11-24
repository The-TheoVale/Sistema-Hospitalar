package clinica.service;

import java.time.LocalDateTime;

import clinica.model.Consulta;
import clinica.model.Medico;
import clinica.model.Paciente;
import clinica.repositorio.ConsultaRepository;
import clinica.repositorio.UtilizaRepository;

public class AgendamentoService {
    private final ConsultaRepository consultaRepositorio;
    private final UtilizaRepository utilizaRepositorio;

    public AgendamentoService(ConsultaRepository consultaRepositorio, UtilizaRepository utilizaRepositorio) {
        this.consultaRepositorio = consultaRepositorio;
        this.utilizaRepositorio = utilizaRepositorio;
    }

    /**
     * Agenda a consulta. Se o médico foi cadastrado depois do paciente (sem vínculo em {@code utiliza}),
     * o vínculo é criado com o plano já usado pelo paciente com os demais médicos.
     * Retorna {@code false} se o horário não estiver disponível.
     */
    public boolean agendar(Medico medico, Paciente paciente, LocalDateTime dataHora) {
        if (!consultaRepositorio.disponibilidadeDoMedico(medico, dataHora)) return false;

        consultaRepositorio.cadastrarConsulta(new Consulta(medico, paciente, dataHora));
        if (!utilizaRepositorio.existeVinculo(medico.getId(), paciente.getId())) {
            utilizaRepositorio.cadastrar(medico.getId(), paciente.getId(),
                    utilizaRepositorio.buscarPlanoDoPaciente(paciente.getId()));
        }
        return true;
    }

    /** Cancela (remove do banco) uma consulta agendada. */
    public boolean cancelar(int idConsulta) {
        return consultaRepositorio.deletar(idConsulta);
    }
}
