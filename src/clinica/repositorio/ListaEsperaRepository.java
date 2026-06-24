package clinica.repositorio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import clinica.model.ListaEspera;
import clinica.model.Medico;
import clinica.model.Paciente;

public class ListaEsperaRepository {
    private List<ListaEspera> listas;
    private MedicoRepository medicoRepositorio;
    private PacienteRepository pacienteRepositorio;

    
    public ListaEsperaRepository(MedicoRepository medicoRepositorio, PacienteRepository pacienteRepositorio) {
        this.listas = new ArrayList<>();
        this.medicoRepositorio = medicoRepositorio;
        this.pacienteRepositorio = pacienteRepositorio;
        carregar();
    }

    
    public void salvar(){
        File pasta = new File("dados");
        pasta.mkdirs();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("dados/listaespera.csv"))) {
            bw.write("idMedico;data;idPaciente");
            bw.newLine();

            for (ListaEspera le : listas) {
                for (Paciente p : le.getFila()) {
                    String linha = String.join(";", le.getMedico().getId(), String.valueOf(le.getData()), p.getId());
                    bw.write(linha);
                    bw.newLine();
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao salvar lista de espera: " + e.getMessage());
        }
    }

    private void carregar() {
        File arquivo = new File("dados/listaespera.csv");
        if (!arquivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            br.readLine(); // pula cabeçalho
            String linha;

            while ((linha = br.readLine()) != null) {
                String[] campos = linha.split(";");

                Medico medico = medicoRepositorio.buscarPorId(campos[0]);
                LocalDate data = LocalDate.parse(campos[1]);
                Paciente paciente = pacienteRepositorio.buscarPorId(campos[2]);

                ListaEspera fila = buscar(medico.getId(), data);
                    if (fila == null){
                        fila = new ListaEspera(medico, data);
                    }
                listas.add(fila);
                fila.entrarNaFila(paciente);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar lista de espera: " + e.getMessage());
        }
}

    public ListaEspera buscar (String idMedico, LocalDate data){
        for (ListaEspera le : listas){
            if (le.getMedico().getId().equals(idMedico) && le.getData().equals(data)) return le;
        }
        return null;
    }

    public int adicionarNaFila (Medico medico, Paciente paciente, LocalDate data){
        ListaEspera fila = buscar(medico.getId(), data);
        if (fila == null){
            fila = new ListaEspera(medico, data);
            listas.add(fila);
        }
        int posicao = fila.getFila().size() + 1;
        fila.entrarNaFila(paciente);
        salvar();
        return posicao;
    }

    public Paciente promoverProximo (String idMedico, LocalDate data){
        ListaEspera lista = buscar(idMedico, data);
        if (lista != null && !lista.estaVazia()){
            Paciente prox = lista.promoverProximo();
            salvar();
            return prox;
        }
        return null;
    }
}
