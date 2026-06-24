package clinica.repositorio;

import clinica.model.Notificacao;
import java.io.*;
import java.util.*;

public class NotificacaoRepository {
    private List<Notificacao> notificacoes = new ArrayList<>();

    public NotificacaoRepository() {
        carregar();
    }

    public void cadastrarNotificacao(Notificacao notificacao) {
        notificacoes.add(notificacao);
        salvar();
    }

    public List<Notificacao> buscarPorPaciente(String idPaciente) {
        List<Notificacao> resultado = new ArrayList<>();
        for (Notificacao n : notificacoes) {
            if (n.getIdPaciente().equals(idPaciente)) {
                resultado.add(n);
            }
        }
        return resultado;
    }

    public void limparPorPaciente(String idPaciente) {
        notificacoes.removeIf(n -> n.getIdPaciente().equals(idPaciente));
        salvar();
    }

    private void salvar() {
        File pasta = new File("dados");
        pasta.mkdirs();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("dados/notificacoes.csv"))) {
            bw.write("idPaciente;mensagem");
            bw.newLine();

            for (Notificacao n : notificacoes) {
                String linha = n.getIdPaciente() + ";" + checarNull(n.getMensagem());
                bw.write(linha);
                bw.newLine();
            }
        } catch (Exception e) {
            System.err.println("Erro ao salvar notificações: " + e.getMessage());
        }
    }

    private void carregar() {
        File arquivo = new File("dados/notificacoes.csv");
        if (!arquivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            br.readLine(); // pula cabeçalho
            String linha;

            while ((linha = br.readLine()) != null) {
                String[] campos = linha.split(";");
                if (campos.length >= 2) {
                    notificacoes.add(new Notificacao(campos[0], campos[1]));
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar notificações: " + e.getMessage());
        }
    }

    private String checarNull(String campo) {
        return (campo == null) ? "" : campo.replace(";", ",");
    }
}

