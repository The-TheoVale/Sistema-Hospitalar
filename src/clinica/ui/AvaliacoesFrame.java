package clinica.ui;

import clinica.model.Medico;
import clinica.model.Avaliacao;
import clinica.model.StatusAvaliacao;
import clinica.repositorio.MedicoRepository;
import clinica.repositorio.PacienteRepository;
import clinica.repositorio.AvaliacaoRepository;
import clinica.repositorio.ConsultaRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AvaliacoesFrame extends JFrame {

    private final Medico medico;
    private final MedicoRepository medicoRepo;
    private final PacienteRepository pacienteRepo;
    private AvaliacaoRepository avaliacaoRepo;
    private JTable tabelaAvaliacoes;
    private DefaultTableModel modeloTabela;

    public AvaliacoesFrame(Medico medico, MedicoRepository medicoRepo, PacienteRepository pacienteRepo) {
        super("Avaliações");
        this.medico = medico;
        this.medicoRepo = medicoRepo;
        this.pacienteRepo = pacienteRepo;
        this.avaliacaoRepo = new AvaliacaoRepository(new ConsultaRepository(medicoRepo, pacienteRepo));
        initComponents();
        carregarAvaliacoes();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Cabeçalho
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel("Sistema de Clínica Médica", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(Color.BLACK);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));

        headerPanel.add(lblTitulo);
        headerPanel.add(separator);

        add(headerPanel, BorderLayout.NORTH);

        // Tabela de avaliações
        modeloTabela = new DefaultTableModel(
            new Object[]{"Data Consulta", "Paciente", "Texto", "Estrelas"}, 0
        );

        tabelaAvaliacoes = new JTable(modeloTabela);
        tabelaAvaliacoes.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(tabelaAvaliacoes);
        add(scrollPane, BorderLayout.CENTER);

        // Botão voltar
        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.addActionListener(e -> dispose());

        add(btnVoltar, BorderLayout.SOUTH);
    }

    private void carregarAvaliacoes() {
        modeloTabela.setRowCount(0);
        List<Avaliacao> avaliacoes = avaliacaoRepo.listarTodos();

        for (Avaliacao a : avaliacoes) {
            // Exibe apenas avaliações concluídas do médico atual
            if (a.getStatus() == StatusAvaliacao.CONCLUIDA &&
                a.getConsulta().getMedico().getId().equals(medico.getId())) {

                modeloTabela.addRow(new Object[]{
                        a.getConsulta().getData(),
                        a.getConsulta().getPaciente().getNome(),
                        a.getTexto(),
                        a.getEstrelas()
                });
            }
        }
    }
}
