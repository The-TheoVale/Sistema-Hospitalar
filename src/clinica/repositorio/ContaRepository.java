package clinica.repositorio;

import clinica.config.Conexao;
import clinica.model.Conta;
import clinica.model.Paciente;
import clinica.model.StatusPagamento;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ContaRepository {
    private final PacienteRepository pacienteRepositorio;

    public ContaRepository(PacienteRepository pacienteRepositorio) {
        this.pacienteRepositorio = pacienteRepositorio;
    }

    public Conta cadastrarConta(Conta c) {
        String sql = """
                INSERT INTO conta (situacao, valor_cobrado, data_de_vencimento, id_usuario, id_consulta)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getSituacao().getValorBanco());
            ps.setBigDecimal(2, java.math.BigDecimal.valueOf(c.getValorCobrado()));
            if (c.getDataDeVencimento() != null) ps.setDate(3, Date.valueOf(c.getDataDeVencimento()));
            else ps.setNull(3, Types.DATE);
            ps.setInt(4, c.getPaciente().getId());
            ps.setInt(5, c.getIdConsulta());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setIdConta(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar conta: " + e.getMessage(), e);
        }
        return c;
    }

    public void pagar(int idConta) {
        String sql = "UPDATE conta SET situacao = 'Paga' WHERE id_conta = ?";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idConta);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao pagar conta: " + e.getMessage(), e);
        }
    }

    public List<Conta> buscarPorPaciente(int idPaciente) {
        List<Conta> lista = new ArrayList<>();
        String sql = "SELECT * FROM conta WHERE id_usuario = ? ORDER BY id_conta";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                Paciente paciente = null;
                while (rs.next()) {
                    if (paciente == null) paciente = pacienteRepositorio.buscarPorId(idPaciente);
                    Date venc = rs.getDate("data_de_vencimento");
                    LocalDate vencimento = (venc != null) ? venc.toLocalDate() : null;
                    lista.add(new Conta(
                            rs.getInt("id_conta"),
                            paciente,
                            rs.getInt("id_consulta"),
                            rs.getBigDecimal("valor_cobrado").doubleValue(),
                            vencimento,
                            StatusPagamento.fromBanco(rs.getString("situacao"))));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar contas: " + e.getMessage(), e);
        }
        return lista;
    }
}
