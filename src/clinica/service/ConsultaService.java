package clinica.service;

import java.time.LocalDate;
import java.util.List;

import clinica.exception.ConsultaInexistenteException;
import clinica.model.Consulta;
import clinica.model.Conta;
import clinica.model.Receita;
import clinica.repositorio.ConsultaRepository;
import clinica.repositorio.ContaRepository;
import clinica.repositorio.ReceitaRepository;
import clinica.repositorio.UtilizaRepository;

public class ConsultaService {
    private static final int DIAS_VENCIMENTO_CONTA = 30;

    private final ConsultaRepository consultaRepositorio;
    private final ContaRepository contaRepositorio;
    private final ReceitaRepository receitaRepositorio;
    private final UtilizaRepository utilizaRepositorio;

    public ConsultaService(ConsultaRepository consultaRepositorio, ContaRepository contaRepositorio,
                           ReceitaRepository receitaRepositorio, UtilizaRepository utilizaRepositorio) {
        this.consultaRepositorio = consultaRepositorio;
        this.contaRepositorio = contaRepositorio;
        this.receitaRepositorio = receitaRepositorio;
        this.utilizaRepositorio = utilizaRepositorio;
    }

    /**
     * Marca a consulta como realizada, salva a receita (se houver prescrição) e gera a conta
     * quando o atendimento é particular.
     */
    public void realizarConsulta(int idConsulta, String descricao, String prescricao,
                                 String orientacao, LocalDate dataValidadeReceita)
            throws ConsultaInexistenteException {

        Consulta consulta = consultaRepositorio.buscarPorId(idConsulta);
        if (consulta == null) {
            throw new ConsultaInexistenteException("Consulta não encontrada: " + idConsulta);
        }

        consulta.realizar(descricao);
        consultaRepositorio.atualizar(consulta);

        if (prescricao != null && !prescricao.isBlank()) {
            receitaRepositorio.cadastrarReceita(new Receita(prescricao, orientacao, dataValidadeReceita, idConsulta));
        }

        Integer idPlano = utilizaRepositorio.buscarPlanoDoPacienteComMedico(
                consulta.getMedico().getId(), consulta.getPaciente().getId());
        if (idPlano == null) {
            Conta conta = new Conta(consulta.getPaciente(), idConsulta,
                    consulta.getMedico().getValorConsulta(), LocalDate.now().plusDays(DIAS_VENCIMENTO_CONTA));
            contaRepositorio.cadastrarConta(conta);
        }
    }

    public List<Consulta> obterProntuario(int idPaciente) {
        return consultaRepositorio.buscarPorPaciente(idPaciente);
    }
}
