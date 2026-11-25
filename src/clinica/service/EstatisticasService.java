package clinica.service;

import java.util.Map;

import clinica.repositorio.ConsultaRepository;

public class EstatisticasService {
    private final ConsultaRepository consultaRepositorio;

    public EstatisticasService(ConsultaRepository consultaRepositorio) {
        this.consultaRepositorio = consultaRepositorio;
    }

    public String especialidadeMaisProcurada() {
        Map<String, Integer> contagem = consultaRepositorio.contarPorEspecialidade();
        return contagem.isEmpty() ? "Nenhuma consulta" : contagem.keySet().iterator().next();
    }

    public int totalConsultasRealizadas() {
        return consultaRepositorio.totalRealizadas();
    }

    /** Nome do médico → quantidade de consultas, em ordem decrescente. */
    public Map<String, Integer> medicosComMinimoDeConsultas(int minimo) {
        return consultaRepositorio.medicosComMinimoDeConsultas(minimo);
    }
    public Map<String, Integer> consultasPorMedico() {
        return consultaRepositorio.contarPorMedico();
    }
}
