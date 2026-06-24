package clinica.service;

import clinica.model.Avaliacao;
import clinica.model.StatusAvaliacao;
import clinica.exception.AvaliacaoInvalidaException;
import clinica.repositorio.AvaliacaoRepository;

public class AvaliacaoService {
    private AvaliacaoRepository avaliacaoRepositorio;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepositorio) {
        this.avaliacaoRepositorio = avaliacaoRepositorio;
    }

    public void avaliar(String identificadorConsulta, String texto, int estrelas) 
            throws AvaliacaoInvalidaException {

        Avaliacao avaliacao = avaliacaoRepositorio.buscarPorConsulta(identificadorConsulta);

        if (avaliacao == null) {
            throw new AvaliacaoInvalidaException("Avaliação não encontrada para essa consulta.");
        }

        if (avaliacao.getStatus() == StatusAvaliacao.CONCLUIDA) {
            throw new AvaliacaoInvalidaException("Essa consulta já foi avaliada.");
        }

        avaliacaoRepositorio.concluirAvaliacao(avaliacao, texto, estrelas);
    }
}