package clinica.ui;

import clinica.model.Consulta;
import clinica.model.Medico;
import clinica.model.Receita;
import clinica.model.StatusConsulta;
import clinica.repositorio.ConsultaRepository;
import clinica.repositorio.ContaRepository;
import clinica.repositorio.PacienteRepository;
import clinica.repositorio.ReceitaRepository;
import clinica.repositorio.UtilizaRepository;
import clinica.service.ConsultaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RealizarConsultaFrame extends JFrame {

    private final Medico medico;
    private final ConsultaRepository consultaRepo;
    private final ReceitaRepository receitaRepo = new ReceitaRepository();
    private final ConsultaService consultaServico;
    private JTable tabelaConsultas;
    private DefaultTableModel modeloTabela;
    private List<Consulta> consultas = new ArrayList<>();

    public RealizarConsultaFrame(Medico medico, PacienteRepository pacienteRepo, ConsultaRepository consultaRepo) {
        super("Realizar Consulta");
        this.medico = medico;
        this.consultaRepo = consultaRepo;
        this.consultaServico = new ConsultaService(consultaRepo, new ContaRepository(pacienteRepo),
                receitaRepo, new UtilizaRepository());
        initComponents();
        carregarConsultas();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initHeader();

        modeloTabela = new DefaultTableModel(
            new Object[]{"Paciente", "Data/Hora", "Consultar"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return column == 2; }
        };

        tabelaConsultas = new JTable(modeloTabela);
        tabelaConsultas.setRowHeight(40);

        tabelaConsultas.getColumn("Consultar").setCellRenderer(new ButtonRenderer());
        tabelaConsultas.getColumn("Consultar").setCellEditor(new ButtonEditor(new JCheckBox()));

        add(new JScrollPane(tabelaConsultas), BorderLayout.CENTER);

        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.addActionListener(e -> dispose());
        add(btnVoltar, BorderLayout.SOUTH);
    }

    private void initHeader() {
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
    }

    private void carregarConsultas() {
        modeloTabela.setRowCount(0);
        try {
            consultas = consultaRepo.buscarAgendadasPorMedico(medico.getId());
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            consultas = new ArrayList<>();
        }
        for (Consulta c : consultas) {
            modeloTabela.addRow(new Object[]{
                    c.getPaciente().getNomeCompleto(),
                    c.getDataHora().format(FormUtil.DATA_HORA),
                    "Consultar"
            });
        }
    }

    /** Só é possível realizar consultas do dia atual. */
    private boolean podeRealizar(int row) {
        return row < consultas.size() && consultas.get(row).getDataHora().toLocalDate().equals(LocalDate.now());
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("Consultar");
            setForeground(Color.WHITE);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            boolean ok = podeRealizar(row);
            setEnabled(ok);
            setBackground(ok ? Color.BLUE : Color.LIGHT_GRAY);
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Consultar");
            button.setForeground(Color.WHITE);
            button.addActionListener(e -> {
                fireEditingStopped();
                realizarConsulta(row);
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.row = row;
            boolean ok = podeRealizar(row);
            button.setEnabled(ok);
            button.setBackground(ok ? Color.BLUE : Color.LIGHT_GRAY);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Consultar";
        }
    }

    private void realizarConsulta(int row) {
        if (!podeRealizar(row)) return;
        Consulta consulta = consultas.get(row);

        getContentPane().removeAll();
        initHeader();

        JPanel mainPanel = new JPanel(new GridLayout(1,2));

        // Painel esquerdo
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        leftPanel.add(new JLabel("Médico: " + medico.getNomeCompleto()));
        leftPanel.add(new JLabel("Paciente: " + consulta.getPaciente().getNomeCompleto()));
        leftPanel.add(new JLabel("Data/Hora: " + consulta.getDataHora().format(FormUtil.DATA_HORA)));

        JTextArea taDescricao = new JTextArea(4,30);
        JTextArea taPrescricao = new JTextArea(4,30);
        JTextArea taOrientacao = new JTextArea(3,30);
        JTextField tfValidade = new JTextField(10);
        tfValidade.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        leftPanel.add(new JLabel("Descrição / diagnóstico:"));
        leftPanel.add(new JScrollPane(taDescricao));
        leftPanel.add(new JLabel("Receita — prescrição (deixe vazio se não houver):"));
        leftPanel.add(new JScrollPane(taPrescricao));
        leftPanel.add(new JLabel("Receita — orientação:"));
        leftPanel.add(new JScrollPane(taOrientacao));
        leftPanel.add(new JLabel("Validade da receita (dd/mm/aaaa):"));
        leftPanel.add(tfValidade);

        JButton btnFinalizar = new JButton("Finalizar Consulta");
        btnFinalizar.addActionListener(e -> {
            try {
                String prescricao = taPrescricao.getText().trim();
                LocalDate validade = FormUtil.parseDataOpcional(tfValidade.getText(), "Validade da receita");
                if (prescricao.isEmpty() && (!taOrientacao.getText().isBlank() || validade != null)) {
                    throw new IllegalArgumentException("Informe a prescrição para salvar a receita.");
                }
                if (validade != null && validade.isBefore(LocalDate.now())) {
                    throw new IllegalArgumentException("A validade da receita não pode ser anterior a hoje.");
                }
                consultaServico.realizarConsulta(consulta.getIdConsulta(),
                        FormUtil.vazioParaNull(taDescricao.getText()), prescricao,
                        FormUtil.vazioParaNull(taOrientacao.getText()), validade);
                JOptionPane.showMessageDialog(this,"Consulta finalizada!");
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,"Erro: "+ex.getMessage());
            }
        });
        leftPanel.add(btnFinalizar);

        // Painel direito
        JPanel rightPanel = new JPanel(new BorderLayout());
        JButton btnVerProntuario = new JButton("Ver Prontuário");
        rightPanel.add(btnVerProntuario, BorderLayout.CENTER);

        btnVerProntuario.addActionListener(e -> {
            rightPanel.removeAll();

            String[] colunas = {"Data/Hora","Médico","Descrição","Prescrição","Orientação","Validade"};
            DefaultTableModel modelo = new DefaultTableModel(colunas,0);
            JTable tabelaProntuario = new JTable(modelo);

            try {
                for (Consulta c : consultaRepo.buscarPorPaciente(consulta.getPaciente().getId())) {
                    if (c.getStatus() != StatusConsulta.REALIZADA) continue;
                    List<Receita> receitas = receitaRepo.buscarPorConsulta(c.getIdConsulta());
                    if (receitas.isEmpty()) {
                        modelo.addRow(new Object[]{c.getDataHora().format(FormUtil.DATA_HORA),
                                c.getMedico().getNomeCompleto(), c.getDescricao(), "", "", ""});
                    }
                    for (Receita r : receitas) {
                        modelo.addRow(new Object[]{c.getDataHora().format(FormUtil.DATA_HORA),
                                c.getMedico().getNomeCompleto(), c.getDescricao(),
                                r.getPrescricao(), r.getOrientacao(), FormUtil.formatar(r.getDataValidade())});
                    }
                }
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }

            rightPanel.add(new JScrollPane(tabelaProntuario), BorderLayout.CENTER);

            JButton btnOcultar = new JButton("Ocultar Prontuário");
            btnOcultar.addActionListener(ev -> {
                rightPanel.removeAll();
                rightPanel.add(btnVerProntuario, BorderLayout.CENTER);
                rightPanel.revalidate();
                rightPanel.repaint();
            });
            rightPanel.add(btnOcultar, BorderLayout.SOUTH);

            rightPanel.revalidate();
            rightPanel.repaint();
        });

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        add(mainPanel, BorderLayout.CENTER);

        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.addActionListener(e -> dispose());
        add(btnVoltar, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }
}
