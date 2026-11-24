package clinica.repositorio;

import clinica.config.Conexao;

import java.sql.*;

/** Tabela associativa {@code utiliza}: qual plano o paciente usa com cada médico (NULL = particular). */
public class UtilizaRepository {

    /** Registra (ou atualiza) o plano usado pelo paciente com o médico. */
    public void cadastrar(int idMedico, int idPaciente, Integer idPlano) {
        String sql = """
                INSERT INTO utiliza (id_usuario_medico, id_usuario_paciente, id_plano)
                VALUES (?, ?, ?)
                ON CONFLICT (id_usuario_medico, id_usuario_paciente)
                DO UPDATE SET id_plano = EXCLUDED.id_plano
                """;
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            ps.setInt(2, idPaciente);
            if (idPlano != null) ps.setInt(3, idPlano);
            else ps.setNull(3, Types.INTEGER);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar plano utilizado: " + e.getMessage(), e);
        }
    }

    /** Registra o plano escolhido no cadastro do paciente para todos os médicos já existentes. */
    public void cadastrarParaTodosMedicos(int idPaciente, Integer idPlano) {
        String sql = """
                INSERT INTO utiliza (id_usuario_medico, id_usuario_paciente, id_plano)
                SELECT id_usuario, ?, ? FROM medico
                ON CONFLICT (id_usuario_medico, id_usuario_paciente)
                DO UPDATE SET id_plano = EXCLUDED.id_plano
                """;
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            if (idPlano != null) ps.setInt(2, idPlano);
            else ps.setNull(2, Types.INTEGER);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar plano do paciente: " + e.getMessage(), e);
        }
    }

    /** Indica se já existe vínculo (plano ou particular) entre o médico e o paciente. */
    public boolean existeVinculo(int idMedico, int idPaciente) {
        String sql = "SELECT 1 FROM utiliza WHERE id_usuario_medico = ? AND id_usuario_paciente = ?";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            ps.setInt(2, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar vínculo: " + e.getMessage(), e);
        }
    }

    /**
     * Plano do paciente deduzido dos vínculos existentes (o mais frequente);
     * {@code null} = particular ou nenhum vínculo.
     */
    public Integer buscarPlanoDoPaciente(int idPaciente) {
        String sql = """
                SELECT id_plano FROM utiliza
                 WHERE id_usuario_paciente = ?
                 GROUP BY id_plano
                 ORDER BY COUNT(*) DESC, id_plano NULLS LAST
                 LIMIT 1
                """;
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return rs.wasNull() ? null : id;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar plano do paciente: " + e.getMessage(), e);
        }
    }

    /** Indica se o médico já atendeu algum paciente pelo plano informado. */
    public boolean medicoAceitaPlano(int idMedico, int idPlano) {
        String sql = "SELECT 1 FROM utiliza WHERE id_usuario_medico = ? AND id_plano = ? LIMIT 1";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            ps.setInt(2, idPlano);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar plano: " + e.getMessage(), e);
        }
    }

    /** Plano usado pelo paciente com o médico; {@code null} = atendimento particular. */
    public Integer buscarPlanoDoPacienteComMedico(int idMedico, int idPaciente) {
        String sql = "SELECT id_plano FROM utiliza WHERE id_usuario_medico = ? AND id_usuario_paciente = ?";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            ps.setInt(2, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return rs.wasNull() ? null : id;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar plano do paciente: " + e.getMessage(), e);
        }
        return null;
    }
}
