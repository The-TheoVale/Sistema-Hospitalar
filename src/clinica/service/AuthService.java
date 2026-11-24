package clinica.service;

import java.time.LocalDate;

import clinica.exception.EspecialidadeInvalidaException;
import clinica.exception.LoginInvalidoException;
import clinica.exception.UsuarioDuplicadoException;
import clinica.model.Cardiologista;
import clinica.model.Cirurgiao;
import clinica.model.ClinicoGeral;
import clinica.model.Medico;
import clinica.model.Paciente;
import clinica.model.Pediatra;
import clinica.model.Usuario;
import clinica.repositorio.MedicoRepository;
import clinica.repositorio.PacienteRepository;
import clinica.repositorio.UtilizaRepository;

public class AuthService {
    private final MedicoRepository medicoRepo;
    private final PacienteRepository pacienteRepo;
    private final UtilizaRepository utilizaRepo;

    public AuthService(MedicoRepository medicoRepo, PacienteRepository pacienteRepo) {
        this(medicoRepo, pacienteRepo, new UtilizaRepository());
    }

    public AuthService(MedicoRepository medicoRepo, PacienteRepository pacienteRepo, UtilizaRepository utilizaRepo) {
        this.medicoRepo = medicoRepo;
        this.pacienteRepo = pacienteRepo;
        this.utilizaRepo = utilizaRepo;
    }

    /**
     * Cadastra o paciente e registra o plano escolhido ({@code null} = particular) em {@code utiliza}
     * para todos os médicos já cadastrados.
     */
    public Paciente cadastrarPaciente(String nome, String sobrenome, String email, String senha,
                                      LocalDate dataNascimento, char sexo, String cpf,
                                      String endereco, String telefone, Integer idPlano)
            throws UsuarioDuplicadoException {
        verificarEmail(email);
        if (pacienteRepo.existeCpf(cpf)) throw new UsuarioDuplicadoException("Já existe paciente com esse CPF");

        Paciente paciente = new Paciente(0, nome, sobrenome, email, senha, dataNascimento, sexo, cpf, endereco, telefone);
        Paciente salvo = pacienteRepo.cadastrarPaciente(paciente);
        utilizaRepo.cadastrarParaTodosMedicos(salvo.getId(), idPlano);
        return salvo;
    }

    /**
     * Cadastra um médico. Os campos específicos da especialidade são:
     * Cardiologista → realizaEcocardiograma; Pediatra → faixaEtariaAtendida, atendeRecemNascidos;
     * Cirurgião → areaCirurgica, tempoExperiencia.
     */
    public Medico cadastrarMedico(String nome, String sobrenome, String email, String senha,
                                  LocalDate dataNascimento, char sexo, String crm,
                                  double valorDaConsulta, String telefoneProfissional, String especialidade,
                                  boolean realizaEcocardiograma,
                                  String faixaEtariaAtendida, boolean atendeRecemNascidos,
                                  String areaCirurgica, short tempoExperiencia)
            throws UsuarioDuplicadoException, EspecialidadeInvalidaException {
        verificarEmail(email);
        if (medicoRepo.existeCrm(crm)) throw new UsuarioDuplicadoException("Já existe médico com esse CRM");

        Medico medico = switch (especialidade) {
            case "Cardiologista" -> new Cardiologista(0, nome, sobrenome, email, senha, dataNascimento, sexo, crm,
                    valorDaConsulta, telefoneProfissional, realizaEcocardiograma);
            case "Pediatra" -> new Pediatra(0, nome, sobrenome, email, senha, dataNascimento, sexo, crm,
                    valorDaConsulta, telefoneProfissional, faixaEtariaAtendida, atendeRecemNascidos);
            case "Cirurgião" -> new Cirurgiao(0, nome, sobrenome, email, senha, dataNascimento, sexo, crm,
                    valorDaConsulta, telefoneProfissional, areaCirurgica, tempoExperiencia);
            case "Clínico Geral" -> new ClinicoGeral(0, nome, sobrenome, email, senha, dataNascimento, sexo, crm,
                    valorDaConsulta, telefoneProfissional);
            default -> throw new EspecialidadeInvalidaException("Especialidade desconhecida");
        };

        return medicoRepo.cadastrarMedico(medico);
    }

    public Usuario login(String email, String senha) throws LoginInvalidoException {
        if (email.isEmpty() || senha.isEmpty()) throw new LoginInvalidoException("Preencha ambos os campos");

        Paciente p = pacienteRepo.buscarPorEmail(email);
        if (p != null && p.getSenha().equals(senha)) return p;

        Medico m = medicoRepo.buscarPorEmail(email);
        if (m != null && m.getSenha().equals(senha)) return m;

        throw new LoginInvalidoException("E-mail ou senha inválidos");
    }

    private void verificarEmail(String email) throws UsuarioDuplicadoException {
        if (pacienteRepo.buscarPorEmail(email) != null || medicoRepo.buscarPorEmail(email) != null) {
            throw new UsuarioDuplicadoException("Já existe usuário com esse e-mail");
        }
    }
}
