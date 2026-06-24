package clinica.repositorio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import clinica.model.Avaliacao;
import clinica.model.Consulta;
import clinica.model.Medico;
import clinica.model.StatusAvaliacao;

public class AvaliacaoRepository {
    private List<Avaliacao> avaliacoes;
    private ConsultaRepository consultaRepositorio;

    public AvaliacaoRepository(ConsultaRepository consultaRepositorio) {
        this.consultaRepositorio = consultaRepositorio;
        avaliacoes = new ArrayList<>();
        carregar();
    }

    public void salvar() {
        File pasta = new File("dados");
        pasta.mkdirs();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("dados/avaliacoes.csv"))) {
            bw.write("identificadorConsulta;texto;estrelas;status");
            bw.newLine();

            for (Avaliacao a : avaliacoes) {
                String linha = String.join(";",
                        a.getConsulta().getIdentificador(),
                        checarNull(a.getTexto()),
                        String.valueOf(a.getEstrelas()),
                        String.valueOf(a.getStatus()));
                bw.write(linha);
                bw.newLine();
            }
        } catch (Exception e) {
            System.err.println("Erro ao salvar avaliações: " + e.getMessage());
        }
    }

    private void carregar() {
        File arquivo = new File("dados/avaliacoes.csv");
        if (!arquivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            br.readLine(); // pula cabeçalho
            String linha;

            while ((linha = br.readLine()) != null) {
                String[] campos = linha.split(";");

                Consulta consulta = consultaRepositorio.buscarPorIdentificador(campos[0]);
                Avaliacao avaliacao = new Avaliacao(consulta);

                if (campos[3].equals("CONCLUIDA")) {
                    avaliacao.responder(campos[1], Integer.parseInt(campos[2]));

                    Medico medico = consulta.getMedico();
                    medico.adicionarAvaliacao(avaliacao);
                }

                avaliacoes.add(avaliacao);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar avaliações: " + e.getMessage());
        }
    }

    private String checarNull(String campo) {
        return (campo == null) ? "" : campo.replace(";", ",");
    }

    public List<Avaliacao> listarTodos() {
        return avaliacoes;
    }

    public Avaliacao buscarPorConsulta(String identificadorConsulta) {
        for (Avaliacao a : avaliacoes) {
            if (a.getConsulta().getIdentificador().equals(identificadorConsulta)) {
                return a;
            }
        }
        return null;
    }

    public void cadastrarAvaliacaoPendente(Consulta consulta) {
        Avaliacao avaliacao = new Avaliacao(consulta);
        avaliacoes.add(avaliacao);
        salvar();
    }

    public void concluirAvaliacao(Avaliacao avaliacao, String texto, int estrelas) {
        avaliacao.responder(texto, estrelas);

        Medico medico = avaliacao.getConsulta().getMedico();
        medico.adicionarAvaliacao(avaliacao);

        salvar();
    }
}