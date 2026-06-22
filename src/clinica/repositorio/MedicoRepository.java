package clinica.repositorio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import clinica.model.Cardiologista;
import clinica.model.ClinicoGeral;
import clinica.model.Medico;
import clinica.model.Paciente;
import clinica.model.Pediatra;


public class MedicoRepository {
    private List<Medico> medicos;

    public MedicoRepository (){
        medicos = new ArrayList<>();
        carregar();
    }
    public void salvar(){
        File pasta = new File ("dados");
        pasta.mkdirs();
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter ("dados/medicos.csv"))) {
            bw.write("id;nome;senha;especialidade;valorConsultaParticular;planosAtendidos");
            bw.newLine();
            for (Medico m : medicos){
                String planosAtendidos = String.join (",", m.getPlanosAtendidos());
                String linha = String.join(";", m.getId(), m.getNome(), m.getSenha(), m.getEspecialidade(), String.valueOf(m.getValorConsultaParticular()), planosAtendidos);
                bw.write(linha);
                bw.newLine();
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao salvar médicos: " + e.getMessage());
        }
    }

    private void carregar(){
        File arquivo = new File("dados/medicos.csv");
        if (!arquivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader("dados/medicos.csv"))) {
            br.readLine();
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] campos = linha.split(";");
                String[] planosAtendidos = campos[5].split(",");
                Medico medico = null;
                switch (campos[3]) {
                    case "Cardiologista":
                        medico = new Cardiologista(campos[0], campos[1], campos[2],
                        Double.parseDouble(campos[4]), Arrays.asList(planosAtendidos));
                    break;

                    case "Pediatra":
                        medico = new Pediatra(campos[0], campos[1], campos[2],
                        Double.parseDouble(campos[4]), Arrays.asList(planosAtendidos));
                    break;

                    case "Clínico Geral":
                        medico = new ClinicoGeral(campos[0], campos[1], campos[2],
                        Double.parseDouble(campos[4]), Arrays.asList(planosAtendidos));
                    break;

                    default:
                        System.err.println("Especialidade desconhecida: " + campos[3]);
                    continue; // pula para a próxima linha do while, sem adicionar nada
                }
                medicos.add(medico);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar médicos: " + e.getMessage());
        }
    }

    public Medico buscarPorId (String id){
        for (Medico m : medicos){
            if (m.getId().equals(id)) return m;
        }
        return null;
    }

    public List<Medico> buscarPorNome (String nome){
        List<Medico> busca = new ArrayList<>();

        for (Medico m : medicos){
            if (m.getNome().toLowerCase().contains(nome.toLowerCase())){
                busca.add(m);
            }
        }
        return busca;
    }

    public List<Medico> buscarPorEspecialidade (String especialidade){
        List<Medico> busca = new ArrayList<>();

        for (Medico m : medicos){
            if (m.getEspecialidade().toLowerCase().contains(especialidade.toLowerCase())){
                busca.add(m);
            }
        }
        return busca;
    }
    
    public List<Medico> listarTodos() {
        return medicos;
    }

    public void cadastrarMedico(Medico medico){
        medicos.add(medico);
        salvar();
    }
}

