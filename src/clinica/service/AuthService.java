package clinica.service;

import java.util.List;

import javax.swing.JOptionPane;

import clinica.exception.EspecialidadeInvalidaException;
import clinica.exception.LoginInvalidoException;
import clinica.exception.UsuarioDuplicadoException;
import clinica.model.Cardiologista;
import clinica.model.ClinicoGeral;
import clinica.model.Medico;
import clinica.model.Paciente;
import clinica.model.Pediatra;
import clinica.model.Usuario;
import clinica.repositorio.MedicoRepository;
import clinica.repositorio.PacienteRepository;
import clinica.ui.MedicoMenuFrame;
import clinica.ui.PacienteMenuFrame;

public class AuthService {
    private MedicoRepository medicoRepo;
    private PacienteRepository pacienteRepo;


    public AuthService(MedicoRepository medicoRepo, PacienteRepository pacienteRepo) {
        this.medicoRepo = medicoRepo;
        this.pacienteRepo = pacienteRepo;
    }

    public Paciente cadastrarPaciente(String id, String nome, String senha, int idade, String planoSaude) throws UsuarioDuplicadoException {
        if (pacienteRepo.buscarPorId(id) != null || medicoRepo.buscarPorId(id) != null) throw new UsuarioDuplicadoException("Já existe usuário com esse id");
        Paciente paciente = new Paciente(id, nome, senha, idade, planoSaude);
        pacienteRepo.cadastrarPaciente(paciente);
        return paciente;
    }

    public Medico cadastrarMedico(String id, String nome, String senha, String especialidade, double valorConsultaParticular, List<String> planosAtendidos) throws UsuarioDuplicadoException, EspecialidadeInvalidaException{
        if (pacienteRepo.buscarPorId(id) != null || medicoRepo.buscarPorId(id) != null) throw new UsuarioDuplicadoException("Já existe usuário com esse id");
        
        Medico medico = null;
        switch (especialidade) {
            case "Cardiologista"-> medico = new Cardiologista(id, nome, senha, valorConsultaParticular, planosAtendidos);
            case "Pediatra" -> medico = new Pediatra(id, nome, senha, valorConsultaParticular, planosAtendidos);
            case "Clínico Geral" -> medico = new ClinicoGeral(id, nome, senha, valorConsultaParticular, planosAtendidos);
            default -> throw new EspecialidadeInvalidaException("Especialidade desconhecida");
        }
        
        medicoRepo.cadastrarMedico(medico);
        return medico;
    }

    public Usuario login(String id, String senha) throws LoginInvalidoException{
        if (id.isEmpty() || senha.isEmpty()) throw new LoginInvalidoException("Preencha ambos os campos");

        Paciente p = pacienteRepo.buscarPorId(id);
        Medico m = medicoRepo.buscarPorId(id);
        
        if (p != null && p.getSenha().equals(senha)) {
            return p;
        } else if (m != null && m.getSenha().equals(senha)) {
            return m;
        } else {
            throw new LoginInvalidoException("Id ou senha inválido");
        }
    }
}
