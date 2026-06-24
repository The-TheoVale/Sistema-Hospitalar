package clinica.service;

import clinica.model.Conta;
import clinica.model.Consulta;
import clinica.exception.ConsultaInexistenteException;
import clinica.repositorio.ConsultaRepository;
import clinica.repositorio.ContaRepository;
import clinica.repositorio.AvaliacaoRepository;

import java.util.List;

public class ConsultaService {
    private ConsultaRepository consultaRepositorio;
    private ContaRepository contaRepositorio;
    private AvaliacaoRepository avaliacaoRepositorio;

    public ConsultaService(ConsultaRepository consultaRepositorio, ContaRepository contaRepositorio,
            AvaliacaoRepository avaliacaoRepositorio) {
        this.consultaRepositorio = consultaRepositorio;
        this.contaRepositorio = contaRepositorio;
        this.avaliacaoRepositorio = avaliacaoRepositorio;
    }

    public void realizarConsulta(String identificadorConsulta, String descricao, String receita, String exames)
            throws ConsultaInexistenteException {

        Consulta consulta = consultaRepositorio.buscarPorIdentificador(identificadorConsulta);
        if (consulta == null) {
            throw new ConsultaInexistenteException("Consulta não encontrada: " + identificadorConsulta);
        }

        consulta.realizar(descricao, receita, exames);

        if (consulta.getValorPago() > 0) {
            Conta conta = new Conta(consulta.getPaciente(), consulta.getValorPago());
            contaRepositorio.cadastrarConta(conta);
        }

        avaliacaoRepositorio.cadastrarAvaliacaoPendente(consulta);

        consultaRepositorio.salvar();
    }

    public List<Consulta> obterProntuario(String idPaciente) {
        return consultaRepositorio.buscarPorPaciente(idPaciente);
    }
}