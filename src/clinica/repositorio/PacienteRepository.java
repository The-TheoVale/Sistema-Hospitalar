package clinica.repositorio;

import clinica.config.Conexao;
import clinica.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PacienteRepository {

    // -----------------------------------------------------------------------
    // Escrita
    // -----------------------------------------------------------------------

    public Paciente cadastrarPaciente(Paciente p) {
        String sql = """
                INSERT INTO paciente
                    (nome, sobrenome, email, senha, data_de_nascimento, sexo, cpf, endereco, telefone)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNome());
            ps.setString(2, p.getSobrenome());
            ps.setString(3, p.getEmail());
            ps.setString(4, p.getSenha());
            ps.setDate(5, Date.valueOf(p.getDataNascimento()));
            ps.setString(6, String.valueOf(p.getSexo()));
            ps.setString(7, p.getCpf());
            ps.setString(8, p.getEndereco());
            ps.setString(9, p.getTelefone());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) p.setIdFromDb(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar paciente: " + e.getMessage(), e);
        }
        return p;
    }

    public void atualizar(Paciente p) {
        String sql = """
                UPDATE paciente
                   SET nome = ?, sobrenome = ?, email = ?, senha = ?,
                       data_de_nascimento = ?, sexo = ?, cpf = ?, endereco = ?, telefone = ?
                 WHERE id_usuario = ?
                """;
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNome());
            ps.setString(2, p.getSobrenome());
            ps.setString(3, p.getEmail());
            ps.setString(4, p.getSenha());
            ps.setDate(5, Date.valueOf(p.getDataNascimento()));
            ps.setString(6, String.valueOf(p.getSexo()));
            ps.setString(7, p.getCpf());
            ps.setString(8, p.getEndereco());
            ps.setString(9, p.getTelefone());
            ps.setInt(10, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar paciente: " + e.getMessage(), e);
        }
    }

    // -----------------------------------------------------------------------
    // Leitura
    // -----------------------------------------------------------------------

    public Paciente buscarPorId(int id) {
        String sql = "SELECT * FROM paciente WHERE id_usuario = ?";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar paciente: " + e.getMessage(), e);
        }
        return null;
    }

    public Paciente buscarPorEmail(String email) {
        String sql = "SELECT * FROM paciente WHERE email = ?";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar paciente por e-mail: " + e.getMessage(), e);
        }
        return null;
    }

    public boolean existeCpf(String cpf) {
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement("SELECT 1 FROM paciente WHERE cpf = ?")) {
            ps.setString(1, cpf);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar CPF: " + e.getMessage(), e);
        }
    }

    public List<Paciente> listarTodos() {
        List<Paciente> lista = new ArrayList<>();
        String sql = "SELECT * FROM paciente ORDER BY nome";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pacientes: " + e.getMessage(), e);
        }
        return lista;
    }

    // -----------------------------------------------------------------------
    // Mapeamento ResultSet → Paciente
    // -----------------------------------------------------------------------

    private Paciente mapear(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_usuario");
        String nome = rs.getString("nome");
        String sobrenome = rs.getString("sobrenome");
        String email = rs.getString("email");
        String senha = rs.getString("senha");
        LocalDate dataNasc = rs.getDate("data_de_nascimento").toLocalDate();
        String sexoStr = rs.getString("sexo");
        char sexo = (sexoStr != null && !sexoStr.isEmpty()) ? sexoStr.charAt(0) : 'O';
        String cpf = rs.getString("cpf");
        String endereco = rs.getString("endereco");
        String telefone = rs.getString("telefone");

        return new Paciente(id, nome, sobrenome, email, senha, dataNasc, sexo, cpf, endereco, telefone);
    }
}
