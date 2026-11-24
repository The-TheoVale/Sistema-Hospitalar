package clinica.repositorio;

import clinica.config.Conexao;
import clinica.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConsultaRepository {
    private final MedicoRepository medicoRepositorio;
    private final PacienteRepository pacienteRepositorio;

    public ConsultaRepository(MedicoRepository medicoRepositorio, PacienteRepository pacienteRepositorio) {
        this.medicoRepositorio = medicoRepositorio;
        this.pacienteRepositorio = pacienteRepositorio;
    }

    // -----------------------------------------------------------------------
    // Escrita
    // -----------------------------------------------------------------------

    public Consulta cadastrarConsulta(Consulta c) {
        String sql = """
                INSERT INTO consulta (data_hora, status, descricao, id_usuario_medico, id_usuario_paciente)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, Timestamp.valueOf(c.getDataHora()));
            ps.setString(2, c.getStatus().getValorBanco());
            ps.setString(3, c.getDescricao());
            ps.setInt(4, c.getMedico().getId());
            ps.setInt(5, c.getPaciente().getId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setIdConsulta(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar consulta: " + e.getMessage(), e);
        }
        return c;
    }

    public void atualizar(Consulta c) {
        String sql = "UPDATE consulta SET status = ?, descricao = ? WHERE id_consulta = ?";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getStatus().getValorBanco());
            ps.setString(2, c.getDescricao());
            ps.setInt(3, c.getIdConsulta());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar consulta: " + e.getMessage(), e);
        }
    }

    /** Cancelamento: a consulta é removida do banco (o banco não possui status "Cancelada"). */
    public boolean deletar(int idConsulta) {
        String sql = "DELETE FROM consulta WHERE id_consulta = ? AND status = 'Agendada'";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idConsulta);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cancelar consulta: " + e.getMessage(), e);
        }
    }

    // -----------------------------------------------------------------------
    // Leitura
    // -----------------------------------------------------------------------

    public Consulta buscarPorId(int idConsulta) {
        List<Consulta> r = consultar("SELECT * FROM consulta WHERE id_consulta = ?", idConsulta);
        return r.isEmpty() ? null : r.get(0);
    }

    public List<Consulta> listarTodos() {
        return consultar("SELECT * FROM consulta ORDER BY data_hora");
    }

    public List<Consulta> buscarPorPaciente(int idPaciente) {
        return consultar("SELECT * FROM consulta WHERE id_usuario_paciente = ? ORDER BY data_hora", idPaciente);
    }

    public List<Consulta> buscarAgendadas(int idPaciente) {
        return consultar("SELECT * FROM consulta WHERE id_usuario_paciente = ? AND status = 'Agendada' ORDER BY data_hora",
                idPaciente);
    }

    public List<Consulta> buscarAgendadasPorMedico(int idMedico) {
        return consultar("SELECT * FROM consulta WHERE id_usuario_medico = ? AND status = 'Agendada' ORDER BY data_hora",
                idMedico);
    }

    /** Verifica se o horário está livre e se o médico ainda não atingiu o limite diário. */
    public boolean disponibilidadeDoMedico(Medico medico, LocalDateTime dataHora) {
        String sqlHorario = "SELECT COUNT(*) FROM consulta WHERE id_usuario_medico = ? AND data_hora = ?";
        String sqlDia = "SELECT COUNT(*) FROM consulta WHERE id_usuario_medico = ? AND data_hora::date = ?";
        try (Connection con = Conexao.conectar()) {
            try (PreparedStatement ps = con.prepareStatement(sqlHorario)) {
                ps.setInt(1, medico.getId());
                ps.setTimestamp(2, Timestamp.valueOf(dataHora));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) return false;
                }
            }
            try (PreparedStatement ps = con.prepareStatement(sqlDia)) {
                ps.setInt(1, medico.getId());
                ps.setDate(2, Date.valueOf(dataHora.toLocalDate()));
                try (ResultSet rs = ps.executeQuery()) {
                    return !rs.next() || rs.getInt(1) < medico.maxConsultasDia();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar disponibilidade: " + e.getMessage(), e);
        }
    }

    /** Horários já ocupados do médico em um dia. */
    public List<LocalDateTime> horariosOcupados(int idMedico, LocalDate dia) {
        List<LocalDateTime> lista = new ArrayList<>();
        String sql = "SELECT data_hora FROM consulta WHERE id_usuario_medico = ? AND data_hora::date = ?";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            ps.setDate(2, Date.valueOf(dia));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(rs.getTimestamp(1).toLocalDateTime());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar horários: " + e.getMessage(), e);
        }
        return lista;
    }

    // -----------------------------------------------------------------------
    // Estatísticas
    // -----------------------------------------------------------------------

    public int totalRealizadas() {
        String sql = "SELECT COUNT(*) FROM consulta WHERE status = 'Realizada'";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar consultas: " + e.getMessage(), e);
        }
    }

    /** Quantidade de consultas por especialidade (Clínico Geral = médico sem tabela filha). */
    public Map<String, Integer> contarPorEspecialidade() {
        String sql = """
                SELECT CASE
                         WHEN ca.id_usuario IS NOT NULL THEN 'Cardiologista'
                         WHEN pe.id_usuario IS NOT NULL THEN 'Pediatra'
                         WHEN ci.id_usuario IS NOT NULL THEN 'Cirurgião'
                         ELSE 'Clínico Geral'
                       END AS especialidade,
                       COUNT(*) AS total
                  FROM consulta c
                  LEFT JOIN cardiologista ca ON ca.id_usuario = c.id_usuario_medico
                  LEFT JOIN pediatra      pe ON pe.id_usuario = c.id_usuario_medico
                  LEFT JOIN cirurgiao     ci ON ci.id_usuario = c.id_usuario_medico
                 GROUP BY 1
                 ORDER BY total DESC
                """;
        Map<String, Integer> mapa = new LinkedHashMap<>();
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) mapa.put(rs.getString("especialidade"), rs.getInt("total"));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar por especialidade: " + e.getMessage(), e);
        }
        return mapa;
    }

    /** Quantidade de consultas por médico (nome completo → total), em ordem decrescente. */
    public Map<String, Integer> contarPorMedico() {
        String sql = """
                SELECT m.nome || COALESCE(' ' || m.sobrenome, '') AS medico, COUNT(*) AS total
                  FROM consulta c
                  JOIN medico m ON m.id_usuario = c.id_usuario_medico
                 GROUP BY m.id_usuario, m.nome, m.sobrenome
                 ORDER BY total DESC
                """;
        Map<String, Integer> mapa = new LinkedHashMap<>();
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) mapa.put(rs.getString("medico"), rs.getInt("total"));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar por médico: " + e.getMessage(), e);
        }
        return mapa;
    }

    // -----------------------------------------------------------------------
    // Mapeamento
    // -----------------------------------------------------------------------

    private List<Consulta> consultar(String sql, Object... params) {
        List<Consulta> lista = new ArrayList<>();
        Map<Integer, Medico> medicos = new HashMap<>();
        Map<Integer, Paciente> pacientes = new HashMap<>();
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idMedico = rs.getInt("id_usuario_medico");
                    int idPaciente = rs.getInt("id_usuario_paciente");
                    Medico medico = medicos.computeIfAbsent(idMedico, medicoRepositorio::buscarPorId);
                    Paciente paciente = pacientes.computeIfAbsent(idPaciente, pacienteRepositorio::buscarPorId);
                    lista.add(new Consulta(
                            rs.getInt("id_consulta"),
                            medico,
                            paciente,
                            rs.getTimestamp("data_hora").toLocalDateTime(),
                            StatusConsulta.fromBanco(rs.getString("status")),
                            rs.getString("descricao")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar consultas: " + e.getMessage(), e);
        }
        return lista;
    }
}
