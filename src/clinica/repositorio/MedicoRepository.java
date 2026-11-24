package clinica.repositorio;

import clinica.config.Conexao;
import clinica.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicoRepository {

    // -----------------------------------------------------------------------
    // Escrita
    // -----------------------------------------------------------------------

    public Medico cadastrarMedico(Medico m) {
        String sqlBase = """
                INSERT INTO medico
                    (nome, sobrenome, email, senha, data_de_nascimento, sexo, crm,
                     valor_da_consulta, telefone_profissional)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection con = Conexao.conectar()) {
            con.setAutoCommit(false);
            try {
                int idGerado;
                try (PreparedStatement ps = con.prepareStatement(sqlBase, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, m.getNome());
                    ps.setString(2, m.getSobrenome());
                    ps.setString(3, m.getEmail());
                    ps.setString(4, m.getSenha());
                    ps.setDate(5, Date.valueOf(m.getDataNascimento()));
                    ps.setString(6, String.valueOf(m.getSexo()));
                    ps.setString(7, m.getCrm());
                    ps.setDouble(8, m.getValorDaConsulta());
                    ps.setString(9, m.getTelefoneProfissional());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) throw new SQLException("ID do médico não gerado.");
                        idGerado = rs.getInt(1);
                    }
                }
                m.setIdFromDb(idGerado);

                // Insere na tabela filha conforme especialidade
                if (m instanceof Cardiologista c) {
                    try (PreparedStatement ps2 = con.prepareStatement(
                            "INSERT INTO cardiologista (id_usuario, realiza_ecocardiograma) VALUES (?, ?)")) {
                        ps2.setInt(1, idGerado);
                        ps2.setBoolean(2, c.isRealizaEcocardiograma());
                        ps2.executeUpdate();
                    }
                } else if (m instanceof Pediatra p) {
                    try (PreparedStatement ps2 = con.prepareStatement(
                            "INSERT INTO pediatra (id_usuario, faixa_etaria_atendida, atende_recem_nascidos) VALUES (?, ?, ?)")) {
                        ps2.setInt(1, idGerado);
                        ps2.setString(2, p.getFaixaEtariaAtendida());
                        ps2.setBoolean(3, p.isAtendeRecemNascidos());
                        ps2.executeUpdate();
                    }
                } else if (m instanceof Cirurgiao ci) {
                    try (PreparedStatement ps2 = con.prepareStatement(
                            "INSERT INTO cirurgiao (id_usuario, area_cirurgica, tempo_experiencia) VALUES (?, ?, ?)")) {
                        ps2.setInt(1, idGerado);
                        ps2.setString(2, ci.getAreaCirurgica());
                        ps2.setShort(3, ci.getTempoExperiencia());
                        ps2.executeUpdate();
                    }
                }
                // ClinicoGeral: apenas tabela base, sem tabela filha

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar médico: " + e.getMessage(), e);
        }
        return m;
    }

    public void atualizar(Medico m) {
        String sqlBase = """
                UPDATE medico
                   SET nome = ?, sobrenome = ?, email = ?, senha = ?, data_de_nascimento = ?,
                       sexo = ?, crm = ?, valor_da_consulta = ?, telefone_profissional = ?
                 WHERE id_usuario = ?
                """;
        try (Connection con = Conexao.conectar()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(sqlBase)) {
                    ps.setString(1, m.getNome());
                    ps.setString(2, m.getSobrenome());
                    ps.setString(3, m.getEmail());
                    ps.setString(4, m.getSenha());
                    ps.setDate(5, Date.valueOf(m.getDataNascimento()));
                    ps.setString(6, String.valueOf(m.getSexo()));
                    ps.setString(7, m.getCrm());
                    ps.setDouble(8, m.getValorDaConsulta());
                    ps.setString(9, m.getTelefoneProfissional());
                    ps.setInt(10, m.getId());
                    ps.executeUpdate();
                }
                if (m instanceof Cardiologista c) {
                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE cardiologista SET realiza_ecocardiograma = ? WHERE id_usuario = ?")) {
                        ps.setBoolean(1, c.isRealizaEcocardiograma());
                        ps.setInt(2, m.getId());
                        ps.executeUpdate();
                    }
                } else if (m instanceof Pediatra p) {
                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE pediatra SET faixa_etaria_atendida = ?, atende_recem_nascidos = ? WHERE id_usuario = ?")) {
                        ps.setString(1, p.getFaixaEtariaAtendida());
                        ps.setBoolean(2, p.isAtendeRecemNascidos());
                        ps.setInt(3, m.getId());
                        ps.executeUpdate();
                    }
                } else if (m instanceof Cirurgiao ci) {
                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE cirurgiao SET area_cirurgica = ?, tempo_experiencia = ? WHERE id_usuario = ?")) {
                        ps.setString(1, ci.getAreaCirurgica());
                        ps.setShort(2, ci.getTempoExperiencia());
                        ps.setInt(3, m.getId());
                        ps.executeUpdate();
                    }
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar médico: " + e.getMessage(), e);
        }
    }

    public boolean existeCrm(String crm) {
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement("SELECT 1 FROM medico WHERE crm = ?")) {
            ps.setString(1, crm);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar CRM: " + e.getMessage(), e);
        }
    }

    // -----------------------------------------------------------------------
    // Leitura
    // -----------------------------------------------------------------------

    public Medico buscarPorId(int id) {
        String sql = "SELECT * FROM medico WHERE id_usuario = ?";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearComEspecialidade(rs, con);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar médico: " + e.getMessage(), e);
        }
        return null;
    }

    public Medico buscarPorEmail(String email) {
        String sql = "SELECT * FROM medico WHERE email = ?";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearComEspecialidade(rs, con);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar médico por e-mail: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Medico> buscarPorNome(String nome) {
        List<Medico> lista = new ArrayList<>();
        String sql = "SELECT * FROM medico WHERE LOWER(nome || ' ' || COALESCE(sobrenome,'')) LIKE ?";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + nome.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapearComEspecialidade(rs, con));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar médico por nome: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<Medico> buscarPorEspecialidade(String especialidade) {
        List<Medico> lista = new ArrayList<>();
        // Determina a tabela filha a usar para filtrar
        String joinTabela = switch (especialidade.toLowerCase()) {
            case "cardiologista" -> "JOIN cardiologista x USING (id_usuario)";
            case "pediatra"      -> "JOIN pediatra x USING (id_usuario)";
            case "cirurgião","cirurgiao" -> "JOIN cirurgiao x USING (id_usuario)";
            default              -> "";
        };
        String sql = "SELECT m.* FROM medico m " + joinTabela + " ORDER BY m.nome";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapearComEspecialidade(rs, con));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar por especialidade: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<Medico> listarTodos() {
        List<Medico> lista = new ArrayList<>();
        String sql = "SELECT * FROM medico ORDER BY nome";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapearComEspecialidade(rs, con));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar médicos: " + e.getMessage(), e);
        }
        return lista;
    }

    // -----------------------------------------------------------------------
    // Mapeamento ResultSet → Medico (com especialidade)
    // -----------------------------------------------------------------------

    private Medico mapearComEspecialidade(ResultSet rs, Connection con) throws SQLException {
        int id = rs.getInt("id_usuario");
        String nome        = rs.getString("nome");
        String sobrenome   = rs.getString("sobrenome");
        String email       = rs.getString("email");
        String senha       = rs.getString("senha");
        LocalDate dataNasc = rs.getDate("data_de_nascimento").toLocalDate();
        String sexoStr     = rs.getString("sexo");
        char sexo = (sexoStr != null && !sexoStr.isEmpty()) ? sexoStr.charAt(0) : 'O';
        String crm         = rs.getString("crm");
        double valor       = rs.getDouble("valor_da_consulta");
        String tel         = rs.getString("telefone_profissional");

        // Tenta Cardiologista
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT realiza_ecocardiograma FROM cardiologista WHERE id_usuario = ?")) {
            ps.setInt(1, id);
            try (ResultSet r2 = ps.executeQuery()) {
                if (r2.next()) {
                    boolean eco = r2.getBoolean("realiza_ecocardiograma");
                    Cardiologista c = new Cardiologista(id, nome, sobrenome, email, senha,
                            dataNasc, sexo, crm, valor, tel, eco);
                    c.setIdFromDb(id);
                    return c;
                }
            }
        }

        // Tenta Pediatra
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT faixa_etaria_atendida, atende_recem_nascidos FROM pediatra WHERE id_usuario = ?")) {
            ps.setInt(1, id);
            try (ResultSet r2 = ps.executeQuery()) {
                if (r2.next()) {
                    String faixa = r2.getString("faixa_etaria_atendida");
                    boolean recemNasc = r2.getBoolean("atende_recem_nascidos");
                    Pediatra p = new Pediatra(id, nome, sobrenome, email, senha,
                            dataNasc, sexo, crm, valor, tel, faixa, recemNasc);
                    p.setIdFromDb(id);
                    return p;
                }
            }
        }

        // Tenta Cirurgiao
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT area_cirurgica, tempo_experiencia FROM cirurgiao WHERE id_usuario = ?")) {
            ps.setInt(1, id);
            try (ResultSet r2 = ps.executeQuery()) {
                if (r2.next()) {
                    String area = r2.getString("area_cirurgica");
                    short tempo = r2.getShort("tempo_experiencia");
                    Cirurgiao ci = new Cirurgiao(id, nome, sobrenome, email, senha,
                            dataNasc, sexo, crm, valor, tel, area, tempo);
                    ci.setIdFromDb(id);
                    return ci;
                }
            }
        }

        // Clínico Geral (sem tabela filha)
        ClinicoGeral cg = new ClinicoGeral(id, nome, sobrenome, email, senha,
                dataNasc, sexo, crm, valor, tel);
        cg.setIdFromDb(id);
        return cg;
    }
}
