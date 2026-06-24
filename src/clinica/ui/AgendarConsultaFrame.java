package clinica.ui;

import clinica.model.Medico;
import clinica.model.Paciente;
import clinica.model.Avaliacao;
import clinica.repositorio.MedicoRepository;
import clinica.repositorio.PacienteRepository;
import clinica.repositorio.ConsultaRepository;
import clinica.repositorio.ListaEsperaRepository;
import clinica.service.AgendamentoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

public class AgendarConsultaFrame extends JFrame {

    private final Paciente paciente;
    private final PacienteRepository pacienteRepo;
    private final MedicoRepository medicoRepo;
    private JTable tabelaMedicos;
    private DefaultTableModel modeloTabela;
    private JTextField tfPesquisa;

    public AgendarConsultaFrame(Paciente paciente, PacienteRepository pacienteRepo, MedicoRepository medicoRepo) {
        super("Agendar Consulta");
        this.paciente = paciente;
        this.pacienteRepo = pacienteRepo;
        this.medicoRepo = medicoRepo;
        initComponents();
        carregarMedicos();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
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

        // Barra de pesquisa
        tfPesquisa = new JTextField("Pesquisar");
        tfPesquisa.setForeground(Color.GRAY);
        tfPesquisa.setPreferredSize(new Dimension(300, 30));
        tfPesquisa.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (tfPesquisa.getText().equals("Pesquisar")) {
                    tfPesquisa.setText("");
                    tfPesquisa.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (tfPesquisa.getText().isEmpty()) {
                    tfPesquisa.setText("Pesquisar");
                    tfPesquisa.setForeground(Color.GRAY);
                }
            }
        });

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> filtrarMedicos(tfPesquisa.getText().trim()));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.add(tfPesquisa);
        searchPanel.add(btnBuscar);

        headerPanel.add(lblTitulo);
        headerPanel.add(separator);
        headerPanel.add(searchPanel);

        add(headerPanel, BorderLayout.NORTH);

        // Tabela de médicos
        modeloTabela = new DefaultTableModel(
            new Object[]{"Nome", "Especialidade", "Média Estrelas", "Últimas Avaliações", "Marcar"}, 0
        );

        tabelaMedicos = new JTable(modeloTabela);
        tabelaMedicos.setRowHeight(40);

        // Renderizador e editor para o botão
        tabelaMedicos.getColumn("Marcar").setCellRenderer(new ButtonRenderer());
        tabelaMedicos.getColumn("Marcar").setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(tabelaMedicos);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void carregarMedicos() {
        modeloTabela.setRowCount(0);

        List<Medico> medicos;
        if (paciente.temPlano()) {
            medicos = medicoRepo.listarTodos().stream()
                    .filter(m -> m.aceitaPlano(paciente.getPlanoSaude()))
                    .collect(Collectors.toList());
        } else {
            medicos = medicoRepo.listarTodos();
        }

        for (Medico m : medicos) {
            String ultimasAvaliacoes = m.getAvaliacoes().stream()
                    .limit(2)
                    .map(Avaliacao::getTexto)
                    .collect(Collectors.joining(" | "));

            modeloTabela.addRow(new Object[]{
                    m.getNome(),
                    m.getEspecialidade(),
                    String.format("%.1f", m.getMediaEstrelas()),
                    ultimasAvaliacoes,
                    "+"
            });
        }
    }

    private void filtrarMedicos(String termo) {
        if (termo.isEmpty() || termo.equals("Pesquisar")) {
            carregarMedicos();
            return;
        }

        List<Medico> medicosFiltrados = medicoRepo.listarTodos().stream()
                .filter(m -> m.getNome().toLowerCase().contains(termo.toLowerCase())
                        || m.getEspecialidade().toLowerCase().contains(termo.toLowerCase()))
                .collect(Collectors.toList());

        modeloTabela.setRowCount(0);
        for (Medico m : medicosFiltrados) {
            String ultimasAvaliacoes = m.getAvaliacoes().stream()
                    .limit(2)
                    .map(Avaliacao::getTexto)
                    .collect(Collectors.joining(" | "));

            modeloTabela.addRow(new Object[]{
                    m.getNome(),
                    m.getEspecialidade(),
                    String.format("%.1f", m.getMediaEstrelas()),
                    ultimasAvaliacoes,
                    "+"
            });
        }
    }

    // Classe para renderizar o botão
    class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setText("+");
        setBackground(Color.GREEN);
        setForeground(Color.WHITE);
    }
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        return this;
    }
}

    // Classe para editar/clicar no botão
   class ButtonEditor extends DefaultCellEditor {
    private JButton button;
    private Medico medicoSelecionado;

    public ButtonEditor(JCheckBox checkBox) {
        super(checkBox);
        button = new JButton("+");
        button.setBackground(Color.GREEN);
        button.setForeground(Color.WHITE);
        button.addActionListener(e -> {
            fireEditingStopped();
            int row = tabelaMedicos.getSelectedRow();
            String nomeMedico = (String) tabelaMedicos.getValueAt(row, 0);
            medicoSelecionado = medicoRepo.buscarPorNome(nomeMedico).get(0);
            mostrarDetalhesMedico(medicoSelecionado);
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return "+";
    }
}

    private void mostrarDetalhesMedico(Medico medico) {
    getContentPane().removeAll();

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

    // Painel central
    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

    // Últimas avaliações
    List<Avaliacao> ultimas = medico.getAvaliacoes().stream().limit(2).collect(Collectors.toList());
    for (Avaliacao a : ultimas) {
        JLabel lblAvaliacao = new JLabel("Nota: " + a.getEstrelas() + " | Texto: " + a.getTexto());
        lblAvaliacao.setFont(new Font("SansSerif", Font.PLAIN, 16));
        centerPanel.add(lblAvaliacao);
    }

    // Calendário
    CalendarioPanel calendario = new CalendarioPanel();
    centerPanel.add(calendario);

    // Botão marcar consulta
    JButton btnMarcar = new JButton("Marcar Consulta");
    btnMarcar.addActionListener(e -> {
        LocalDate data = calendario.getDataSelecionada();
        if (data == null) {
            JOptionPane.showMessageDialog(this, "Selecione um dia válido.");
            return;
        }

        ConsultaRepository consultaRepo = new ConsultaRepository(medicoRepo, pacienteRepo);
        ListaEsperaRepository listaRepo = new ListaEsperaRepository(medicoRepo, pacienteRepo);
        AgendamentoService service = new AgendamentoService(consultaRepo, listaRepo);

        int resultado = service.agendar(medico, paciente, data);
        if (resultado == 0) {
            JOptionPane.showMessageDialog(this, "Consulta marcada com sucesso!");
        } else {
            JOptionPane.showMessageDialog(this, "Sem vagas, você entrou na lista de espera. Posição: " + resultado);
        }
    });
    centerPanel.add(btnMarcar);

    add(centerPanel, BorderLayout.CENTER);

    revalidate();
    repaint();
}
class CalendarioPanel extends JPanel {
    private YearMonth mesAtual;
    private ButtonGroup grupoDias;
    private JPanel diasPanel;
    private JLabel lblMes;
    private LocalDate hoje;

    public CalendarioPanel() {
        setLayout(new BorderLayout());
        mesAtual = YearMonth.now();
        hoje = LocalDate.now();

        // Topo com setas e nome do mês
        JPanel topo = new JPanel(new BorderLayout());
        JButton btnEsquerda = new JButton("<");
        JButton btnDireita = new JButton(">");
        lblMes = new JLabel(mesAtual.getMonth().toString() + " " + mesAtual.getYear(), SwingConstants.CENTER);
        lblMes.setFont(new Font("SansSerif", Font.BOLD, 18));

        topo.add(btnEsquerda, BorderLayout.WEST);
        topo.add(lblMes, BorderLayout.CENTER);
        topo.add(btnDireita, BorderLayout.EAST);

        add(topo, BorderLayout.NORTH);

        // Painel dos dias
        diasPanel = new JPanel(new GridLayout(0,7));
        add(diasPanel, BorderLayout.CENTER);

        grupoDias = new ButtonGroup();
        preencherDias();

        // Ações das setas
        btnEsquerda.addActionListener(e -> {
            mesAtual = mesAtual.minusMonths(1);
            preencherDias();
        });
        btnDireita.addActionListener(e -> {
            mesAtual = mesAtual.plusMonths(1);
            preencherDias();
        });
    }

    private void preencherDias() {
        diasPanel.removeAll();
        grupoDias = new ButtonGroup();
        lblMes.setText(mesAtual.getMonth().toString() + " " + mesAtual.getYear());

        for (int dia = 1; dia <= mesAtual.lengthOfMonth(); dia++) {
            LocalDate data = LocalDate.of(mesAtual.getYear(), mesAtual.getMonth(), dia);
            JRadioButton rbDia = new JRadioButton(String.valueOf(dia));

            // só habilita dias >= hoje
            rbDia.setEnabled(!data.isBefore(hoje));

            grupoDias.add(rbDia);
            diasPanel.add(rbDia);
        }
        diasPanel.revalidate();
        diasPanel.repaint();
    }

    public LocalDate getDataSelecionada() {
        for (Component c : diasPanel.getComponents()) {
            if (c instanceof JRadioButton rb && rb.isSelected()) {
                int dia = Integer.parseInt(rb.getText());
                return LocalDate.of(mesAtual.getYear(), mesAtual.getMonth(), dia);
            }
        }
        return null;
    }
}

}