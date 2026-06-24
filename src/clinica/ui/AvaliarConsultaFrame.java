package clinica.ui;

import clinica.model.Avaliacao;
import clinica.model.Consulta;
import clinica.model.Paciente;
import clinica.model.StatusAvaliacao;
import clinica.repositorio.AvaliacaoRepository;
import clinica.repositorio.ConsultaRepository;
import clinica.service.AvaliacaoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AvaliarConsultaFrame extends JFrame {

    private final AvaliacaoRepository avaliacaoRepo;
    private final AvaliacaoService avaliacaoService;
    private final Paciente paciente;
    private JTable tabela;
    private DefaultTableModel modelo;

    public AvaliarConsultaFrame(Paciente paciente, ConsultaRepository consultaRepo) {
        super("Avaliar Consulta");
        this.paciente = paciente;
        this.avaliacaoRepo = new AvaliacaoRepository(consultaRepo);
        this.avaliacaoService = new AvaliacaoService(avaliacaoRepo);

        initComponents();
        carregarAvaliacoesPendentes();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 500);
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
        modelo = new DefaultTableModel(new Object[]{"Data", "Médico", "Texto", "Estrelas", "Enviar"}, 0);
        tabela = new JTable(modelo);
        tabela.setRowHeight(40);

        // Define JComboBox para coluna de estrelas (1 a 5)
        String[] opcoesEstrelas = {"1","2","3","4","5"};
        JComboBox<String> comboEstrelas = new JComboBox<>(opcoesEstrelas);
        tabela.getColumn("Estrelas").setCellEditor(new DefaultCellEditor(comboEstrelas));

        // Renderizador e editor para botão Enviar
        tabela.getColumn("Enviar").setCellRenderer(new ButtonRenderer());
        tabela.getColumn("Enviar").setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(tabela);
        add(scrollPane, BorderLayout.CENTER);

        // Botão voltar
        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.addActionListener(e -> dispose());
        add(btnVoltar, BorderLayout.SOUTH);
    }

    private void carregarAvaliacoesPendentes() {
        modelo.setRowCount(0);
        for (Avaliacao a : avaliacaoRepo.listarTodos()) {
            if (a.getStatus() == StatusAvaliacao.PENDENTE 
                && a.getConsulta().getPaciente().getId().equals(paciente.getId())) {
                
                Consulta c = a.getConsulta();
                modelo.addRow(new Object[]{
                        c.getData(),
                        c.getMedico().getNome(),
                        "",
                        "1", // valor inicial de estrelas
                        "Enviar"
                });
            }
        }
    }

    // Renderizador do botão
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("Enviar");
            setBackground(Color.BLUE);
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
        private JButton button;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Enviar");
            button.setBackground(Color.BLUE);
            button.setForeground(Color.WHITE);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    enviarAvaliacao(row);
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
            return "Enviar";
        }
    }

    private void enviarAvaliacao(int row) {
        String dataStr = tabela.getValueAt(row, 0).toString();
        String medicoNome = (String) tabela.getValueAt(row, 1);
        String texto = (String) tabela.getValueAt(row, 2);
        int estrelas = Integer.parseInt(tabela.getValueAt(row, 3).toString());

        if (estrelas < 1 || estrelas > 5) {
            JOptionPane.showMessageDialog(this, "Escolha entre 1 e 5 estrelas.");
            return;
        }

        Avaliacao avaliacao = avaliacaoRepo.listarTodos().stream()
                .filter(a -> a.getConsulta().getData().toString().equals(dataStr)
                        && a.getConsulta().getMedico().getNome().equals(medicoNome)
                        && a.getConsulta().getPaciente().getId().equals(paciente.getId()))
                .findFirst().orElse(null);

        if (avaliacao != null) {
            try {
                avaliacaoService.avaliar(avaliacao.getConsulta().getIdentificador(), texto, estrelas);
                JOptionPane.showMessageDialog(this, "Avaliação enviada!");
                carregarAvaliacoesPendentes();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        }
    }
}
