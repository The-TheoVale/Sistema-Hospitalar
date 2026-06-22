package clinica.repositorio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import clinica.model.Paciente;

import clinica.repositorio.PacienteRepository;
public class PacienteRepository {
    private List<Paciente> pacientes;

    public PacienteRepository() {
        pacientes = new ArrayList<>();
        carregar();
    }

    public void salvar(){
        File pasta = new File ("dados");
        pasta.mkdirs();
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter ("dados/pacientes.csv"))) {
            bw.write("id;nome;senha;idade;planoSaude");
            bw.newLine();
            for (Paciente p : pacientes){
                String linha = String.join(";", p.getId(), p.getNome(), p.getSenha(), String.valueOf(p.getIdade()), p.getPlanoSaude());
                bw.write(linha);
                bw.newLine();
            }
        } catch (Exception e) {
            System.err.println("Erro ao salvar pacientes: " + e.getMessage());
        }
    }

    private void carregar() {
        File arquivo = new File("dados/pacientes.csv");
        if (!arquivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            br.readLine(); // pula o cabeçalho

            while ((linha = br.readLine()) != null) {
                String[] campos = linha.split(";");

                Paciente p = new Paciente(campos[0], campos[1], campos[2], Integer.parseInt(campos[3]), campos[4]);
                pacientes.add(p);
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar pacientes: " + e.getMessage());
        }
    }
    
    public Paciente buscarPorId (String id){
        for (Paciente p : pacientes){
            if (p.getId().equals(id)) return p;
        }
        return null;
    }

    public List<Paciente> listarTodos() {
        return pacientes;
    }

    public void cadastrarPaciente(Paciente paciente){
        pacientes.add(paciente);
        salvar();
    }
}
