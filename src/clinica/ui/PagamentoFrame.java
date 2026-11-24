package clinica.ui;

import clinica.model.Conta;
import clinica.model.Paciente;
import clinica.model.StatusPagamento;
import clinica.repositorio.ContaRepository;
import clinica.repositorio.PacienteRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PagamentoFrame extends JFrame {

    private final Paciente paciente;
    private final ContaRepository contaRepo;
    private JTable tabela;
    private DefaultTableModel modelo;
    private List<Conta> contas = new ArrayList<>();

    public PagamentoFrame(Paciente paciente, PacienteRepository pacienteRepo) {
        super("Pagamento");
        this.paciente = paciente;
        this.contaRepo = new ContaRepository(pacienteRepo);

        initComponents();
        carregarContas();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 400);
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

        // Tabela
        modelo = new DefaultTableModel(new Object[]{"Valor", "Vencimento", "Situação", "Pagar"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return column == 3; }
        };
        tabela = new JTable(modelo);
        tabela.setRowHeight(40);

        // Renderizador e editor para botão Pagar
        tabela.getColumn("Pagar").setCellRenderer(new ButtonRenderer());
        tabela.getColumn("Pagar").setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(tabela);
        add(scrollPane, BorderLayout.CENTER);

        // Botão voltar
        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.addActionListener(e -> dispose());
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        southPanel.add(btnVoltar);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void carregarContas() {
        modelo.setRowCount(0);
        try {
            contas = contaRepo.buscarPorPaciente(paciente.getId());
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            contas = new ArrayList<>();
        }
        for (Conta c : contas) {
            modelo.addRow(new Object[]{
                    String.format("R$ %.2f", c.getValorCobrado()),
                    FormUtil.formatar(c.getDataDeVencimento()),
                    c.getSituacao().getValorBanco(),
                    "Pagar"
            });
        }
    }

    // Renderizador do botão
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("Pagar");
            setBackground(Color.ORANGE);
            setForeground(Color.BLACK);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            boolean paga = row < contas.size() && contas.get(row).getSituacao() == StatusPagamento.PAGA;
            setEnabled(!paga);
            setBackground(paga ? Color.LIGHT_GRAY : Color.ORANGE);
            return this;
        }
    }

    // Editor do botão
    class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Pagar");
            button.setBackground(Color.ORANGE);
            button.setForeground(Color.BLACK);
            button.addActionListener(e -> {
                fireEditingStopped();
                pagarConta(row);
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
            return "Pagar";
        }
    }

    private void pagarConta(int row) {
        if (row >= contas.size()) return;
        Conta conta = contas.get(row);
        if (conta.getSituacao() == StatusPagamento.PENDENTE) {
            try {
                contaRepo.pagar(conta.getIdConta());
                conta.pagar();
                JOptionPane.showMessageDialog(this, String.format("Conta de R$ %.2f paga com sucesso!", conta.getValorCobrado()));
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
            carregarContas();
        } else {
            JOptionPane.showMessageDialog(this, "Essa conta já está paga.");
        }
    }
}
