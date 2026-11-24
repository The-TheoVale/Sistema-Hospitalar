package clinica.ui;

import clinica.model.Consulta;
import clinica.model.Paciente;
import clinica.repositorio.ConsultaRepository;
import clinica.repositorio.UtilizaRepository;
import clinica.service.AgendamentoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CancelarAgendamentoFrame extends JFrame {

    private final Paciente paciente;
    private final ConsultaRepository consultaRepo;
    private final AgendamentoService agendamentoService;
    private JTable tabelaConsultas;
    private DefaultTableModel modeloTabela;
    private List<Consulta> consultas = new ArrayList<>();

    public CancelarAgendamentoFrame(Paciente paciente, ConsultaRepository consultaRepo) {
        super("Cancelar Agendamento");
        this.paciente = paciente;
        this.consultaRepo = consultaRepo;
        this.agendamentoService = new AgendamentoService(consultaRepo, new UtilizaRepository());
        initComponents();
        carregarConsultas();
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

        // Tabela de consultas
        modeloTabela = new DefaultTableModel(
            new Object[]{"Médico", "Especialidade", "Data/Hora", "Ação"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return column == 3; }
        };

        tabelaConsultas = new JTable(modeloTabela);
        tabelaConsultas.setRowHeight(40);

        // Renderizador e editor para o botão cancelar
        tabelaConsultas.getColumn("Ação").setCellRenderer(new ButtonRenderer());
        tabelaConsultas.getColumn("Ação").setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(tabelaConsultas);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void carregarConsultas() {
        modeloTabela.setRowCount(0);
        try {
            consultas = consultaRepo.buscarAgendadas(paciente.getId());
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            consultas = new ArrayList<>();
        }

        for (Consulta c : consultas) {
            modeloTabela.addRow(new Object[]{
                    c.getMedico().getNomeCompleto(),
                    c.getMedico().getEspecialidade(),
                    c.getDataHora().format(FormUtil.DATA_HORA),
                    "-"
            });
        }
    }

    // Renderizador do botão
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("-");
            setBackground(Color.RED);
            setForeground(Color.WHITE);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // Editor do botão
    class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("-");
            button.setBackground(Color.RED);
            button.setForeground(Color.WHITE);
            button.addActionListener(e -> {
                fireEditingStopped();
                cancelarConsulta(row);
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.row = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "-";
        }
    }

    private void cancelarConsulta(int row) {
        if (row >= consultas.size()) return;
        Consulta consulta = consultas.get(row);

        int opcao = JOptionPane.showConfirmDialog(this,
                "Cancelar a consulta de " + consulta.getDataHora().format(FormUtil.DATA_HORA) + "?",
                "Confirmar cancelamento", JOptionPane.YES_NO_OPTION);
        if (opcao != JOptionPane.YES_OPTION) return;

        try {
            if (agendamentoService.cancelar(consulta.getIdConsulta())) {
                JOptionPane.showMessageDialog(this, "Consulta cancelada com sucesso!");
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao cancelar consulta.");
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        carregarConsultas();
    }
}
