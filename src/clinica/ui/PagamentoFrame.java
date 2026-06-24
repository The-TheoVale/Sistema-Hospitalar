package clinica.ui;

import clinica.model.Paciente;
import clinica.model.Conta;
import clinica.model.StatusPagamento;
import clinica.repositorio.ContaRepository;
import clinica.repositorio.PacienteRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PagamentoFrame extends JFrame {

    private final Paciente paciente;
    private final ContaRepository contaRepo;
    private JTable tabela;
    private DefaultTableModel modelo;

    public PagamentoFrame(Paciente paciente, PacienteRepository pacienteRepo) {
        super("Pagamento");
        this.paciente = paciente;
        this.contaRepo = new ContaRepository(pacienteRepo);

        initComponents();
        carregarContasPendentes();
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
        modelo = new DefaultTableModel(new Object[]{"Valor", "Situação", "Pagar"}, 0);
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

    private void carregarContasPendentes() {
        modelo.setRowCount(0);
        List<Conta> contas = contaRepo.buscarPorPaciente(paciente.getId());
        for (Conta c : contas) {
            modelo.addRow(new Object[]{
                    c.getValor(),
                    c.getSituacao(),
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
            Conta conta = contaRepo.buscarPorPaciente(paciente.getId()).get(row);
            if (conta.getSituacao() == StatusPagamento.PAGA) {
                setEnabled(false);
                setBackground(Color.LIGHT_GRAY);
            } else {
                setEnabled(true);
                setBackground(Color.ORANGE);
            }
            return this;
        }
    }

    // Editor do botão
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Pagar");
            button.setBackground(Color.ORANGE);
            button.setForeground(Color.BLACK);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    pagarConta(row);
                }
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
        List<Conta> contas = contaRepo.buscarPorPaciente(paciente.getId());
        if (row < contas.size()) {
            Conta conta = contas.get(row);
            if (conta.getSituacao() == StatusPagamento.PENDENTE) {
                conta.pagar();
                contaRepo.salvar();
                JOptionPane.showMessageDialog(this, "Conta de R$" + conta.getValor() + " paga com sucesso!");
                carregarContasPendentes();
            } else {
                JOptionPane.showMessageDialog(this, "Essa conta já está paga.");
            }
        }
    }
}
