package clinica.repositorio;

import clinica.config.Conexao;
import clinica.model.Plano;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlanoRepository {

    public List<Plano> listarTodos() {
        List<Plano> lista = new ArrayList<>();
        String sql = "SELECT * FROM plano ORDER BY nome_do_plano";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar planos: " + e.getMessage(), e);
        }
        return lista;
    }

    public Plano buscarPorId(int idPlano) {
        String sql = "SELECT * FROM plano WHERE id_plano = ?";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPlano);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar plano: " + e.getMessage(), e);
        }
        return null;
    }

    public Plano buscarPorNome(String nome) {
        String sql = "SELECT * FROM plano WHERE nome_do_plano = ?";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nome);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar plano: " + e.getMessage(), e);
        }
        return null;
    }

    private Plano mapear(ResultSet rs) throws SQLException {
        return new Plano(rs.getInt("id_plano"), rs.getString("nome_do_plano"), rs.getString("telefone_de_contato"));
    }
}
