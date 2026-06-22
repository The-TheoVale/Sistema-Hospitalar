package clinica.repositorio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import clinica.model.Consulta;
import clinica.model.Medico;
import clinica.model.Paciente;
import clinica.model.StatusConsulta;

public class ConsultaRepository {
    private MedicoRepository medicosRepositorio;
    private PacienteRepository pacienteRepository;
    private List<Consulta> consultas;

    public ConsultaRepository(MedicoRepository medicosRepositorio, PacienteRepository pacienteRepository) {
        this.medicosRepositorio = medicosRepositorio;
        this.pacienteRepository = pacienteRepository;
        this.consultas = new ArrayList<>();
        carregar();
    }

    public void salvar(){
        File pasta = new File("dados");
        pasta.mkdirs();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("dados/consultas.csv"))) {
            bw.write("idMedico;idPaciente;data;status;descricao;receita;exames;valorPago");
            bw.newLine();

            for (Consulta c : consultas){
                String descricaoFile = checarNull(c.getDescricao());
                String receitaFile = checarNull(c.getReceita());
                String examesFile = checarNull(c.getExames());
                String linha = String.join(";", c.getMedico().getId(), c.getPaciente().getId(), String.valueOf(c.getData()), String.valueOf(c.getStatus()), descricaoFile, receitaFile, examesFile, String.valueOf(c.getValorPago()));
                bw.write(linha);
                bw.newLine();
            }

            
        } catch (Exception e) {
            System.err.println("Erro ao salvar consulta: " + e.getMessage());
        }
    }


    private void carregar(){
        File arquivo = new File("dados/consultas.csv");
        if (!arquivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            br.readLine();
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] campos = linha.split(";");
                Medico medico = medicosRepositorio.buscarPorId(campos[0]);
                Paciente paciente = pacienteRepository.buscarPorId(campos[1]);
                LocalDate data = LocalDate.parse(campos[2]);
                Consulta consulta = new Consulta(medico, paciente, data);

                if (campos[3].equals("REALIZADA")){
                    consulta.realizar(campos[4], campos[5], campos[6]);
                    consulta.setValorPago(Double.parseDouble(campos[7]));
                } else if (campos[3].equals("CANCELADA")){
                    consulta.setStatus(StatusConsulta.CANCELADA);
                }
                consultas.add(consulta);
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao carregar consultas: " + e.getMessage());
        }
    }

    private String checarNull(String campo) {
        return (campo == null) ? "" : campo.replace(";", ",");
    }

    public Consulta buscarPorIdentificador(String identificador){
        for (Consulta c : consultas){
            if (c.getIdentificador().equals(identificador)) return c;
        }
        return null;
    }

    public List<Consulta> listarTodos(){
        return consultas;
    }

    public void cadastrarConsulta( Consulta consulta){
        consultas.add(consulta);
        salvar();
    }

    public List<Consulta> buscarPorPaciente (String idPaciente){
        List<Consulta> busca = new ArrayList<>();

        for (Consulta c : consultas){
            if (c.getPaciente().getId().equals(idPaciente)){
                busca.add(c);
            } 
        }
        return busca;
    }
}
