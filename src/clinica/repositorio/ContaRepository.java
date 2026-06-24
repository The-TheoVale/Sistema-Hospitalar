package clinica.repositorio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import clinica.model.Conta;
import clinica.model.Paciente;
import clinica.model.StatusPagamento;

public class ContaRepository {
    private List<Conta> contas;
    private PacienteRepository pacienteRepositorio;

    public ContaRepository(PacienteRepository pacienteRepositorio) {
        this.pacienteRepositorio = pacienteRepositorio;
        contas = new ArrayList<>();
        carregar();
    }

    public void salvar() {
        File pasta = new File("dados");
        pasta.mkdirs();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("dados/contas.csv"))) {
            bw.write("idPaciente;valor;situacao");
            bw.newLine();

            for (Conta c : contas) {
                String linha = String.join(";", c.getPaciente().getId(),
                        String.valueOf(c.getValor()), String.valueOf(c.getSituacao()));
                bw.write(linha);
                bw.newLine();
            }
        } catch (Exception e) {
            System.err.println("Erro ao salvar contas: " + e.getMessage());
        }
    }

    private void carregar() {
        File arquivo = new File("dados/contas.csv");
        if (!arquivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            br.readLine(); // pula cabeçalho
            String linha;

            while ((linha = br.readLine()) != null) {
                String[] campos = linha.split(";");

                Paciente paciente = pacienteRepositorio.buscarPorId(campos[0]);
                double valor = Double.parseDouble(campos[1]);

                Conta conta = new Conta(paciente, valor);

                if (campos[2].equals("PAGA")) {
                    conta.pagar();
                }

                contas.add(conta);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar contas: " + e.getMessage());
        }
    }

    public List<Conta> listarTodos() {
        return contas;
    }

    public void cadastrarConta(Conta conta) {
        contas.add(conta);
        salvar();
    }

    public List<Conta> buscarPorPaciente(String idPaciente) {
        List<Conta> resultado = new ArrayList<>();

        for (Conta c : contas) {
            if (c.getPaciente().getId().equals(idPaciente)) {
                resultado.add(c);
            }
        }

        return resultado;
    }
}