package clinica.service;

import clinica.repositorio.MedicoRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clinica.model.Consulta;
import clinica.model.Medico;
import clinica.model.StatusConsulta;
import clinica.repositorio.ConsultaRepository;

public class EstatisticasService {
    private MedicoRepository medicoRepositorio;
    private ConsultaRepository consultaRepositorio;

    public EstatisticasService(MedicoRepository medicoRepositorio, ConsultaRepository consultaRepositorio) {
        this.medicoRepositorio = medicoRepositorio;
        this.consultaRepositorio = consultaRepositorio;
    }

    public Medico medicoMaisBemAvaliado(){
        List<Medico> todos = medicoRepositorio.listarTodos();
        if (todos.isEmpty()) return null;
        Medico melhor = todos.get(0);
        for (Medico m : todos){
            if (m.getMediaEstrelas() > melhor.getMediaEstrelas()) melhor = m;
        }
        return melhor;
    }

    public String especialidadeMaisProcurada(){
        int contGeral = 0;
        int contPed = 0;
        int contCard = 0;

        for (Consulta c : consultaRepositorio.listarTodos()){
            String especialidade = c.getMedico().getEspecialidade();

            switch (especialidade) {
                case "Clínico Geral" -> contGeral++;
                case "Pediatra" -> contPed++;
                case "Cardiologista" -> contCard++;
            }
        }

        if (contGeral >= contPed && contGeral >= contCard) return "Clínico Geral";
        if (contPed > contGeral  && contPed > contCard) return "Pediatra";
        return "Cardiologista";
    }
    
    public int totalConsultasRealizadas(){
        int contador = 0;
        for (Consulta c : consultaRepositorio.listarTodos()){
            if (c.getStatus().equals(StatusConsulta.REALIZADA)) contador++;
        }
        return contador;
    }

    public double mediaAvaliacoes(){
        List<Medico> todos = medicoRepositorio.listarTodos();
        int cont = 0;
        double soma = 0;
        for (Medico m : todos){
            if (m.getAvaliacoes().isEmpty()) continue;
            soma += m.getMediaEstrelas();
            cont++;
        }

        if (cont == 0) return 0;

        return soma / cont;
    }

    public Map<String, Integer> consultasPorMedico(){
        Map<String, Integer> contagem = new HashMap<>();

        for (Consulta c : consultaRepositorio.listarTodos()){
            String idMedico = c.getMedico().getId();
            contagem.put(idMedico, contagem.getOrDefault(idMedico, 0) + 1);
        }

        return contagem;
    }
}