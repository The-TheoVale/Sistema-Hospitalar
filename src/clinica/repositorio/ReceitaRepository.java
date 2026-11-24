package clinica.repositorio;

import clinica.config.Conexao;
import clinica.model.Receita;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReceitaRepository {

    public Receita cadastrarReceita(Receita r) {
        String sql = "INSERT INTO receita (prescricao, orientacao, data_validade, id_consulta) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getPrescricao());
            ps.setString(2, r.getOrientacao());
            if (r.getDataValidade() != null) ps.setDate(3, Date.valueOf(r.getDataValidade()));
            else ps.setNull(3, Types.DATE);
            ps.setInt(4, r.getIdConsulta());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) r.setIdReceita(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar receita: " + e.getMessage(), e);
        }
        return r;
    }

    public List<Receita> buscarPorConsulta(int idConsulta) {
        List<Receita> lista = new ArrayList<>();
        String sql = "SELECT * FROM receita WHERE id_consulta = ? ORDER BY id_receita";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idConsulta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date validade = rs.getDate("data_validade");
                    lista.add(new Receita(
                            rs.getInt("id_receita"),
                            rs.getString("prescricao"),
                            rs.getString("orientacao"),
                            validade != null ? validade.toLocalDate() : null,
                            rs.getInt("id_consulta")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar receitas: " + e.getMessage(), e);
        }
        return lista;
    }
}
