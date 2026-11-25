package clinica.ui;

import clinica.model.Medico;
import clinica.model.Paciente;
import clinica.model.Plano;
import clinica.repositorio.ConsultaRepository;
import clinica.repositorio.MedicoRepository;
import clinica.repositorio.PlanoRepository;
import clinica.repositorio.UtilizaRepository;
import clinica.service.AgendamentoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AgendarConsultaFrame extends JFrame {

    private static final int HORA_INICIO = 8;
    private static final int HORA_FIM = 17;
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final Paciente paciente;
    private final MedicoRepository medicoRepo;
    private final ConsultaRepository consultaRepo;
    private final PlanoRepository planoRepo = new PlanoRepository();
    private final UtilizaRepository utilizaRepo = new UtilizaRepository();
    private JTable tabelaMedicos;
    private DefaultTableModel modeloTabela;
    private JTextField tfPesquisa;
    private List<Medico> medicosExibidos = new ArrayList<>();

    public AgendarConsultaFrame(Paciente paciente, MedicoRepository medicoRepo, ConsultaRepository consultaRepo) {
        super("Agendar Consulta");
        this.paciente = paciente;
        this.medicoRepo = medicoRepo;
        this.consultaRepo = consultaRepo;
        initComponents();
        carregarMedicos();
    }

    private JPanel criarCabecalho() {
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
        return headerPanel;
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel headerPanel = criarCabecalho();

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
        headerPanel.add(searchPanel);

        add(headerPanel, BorderLayout.NORTH);

        // Tabela de médicos
        modeloTabela = new DefaultTableModel(
            new Object[]{"Nome", "Especialidade", "CRM", "Valor (particular)", "Marcar"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return column == 4; }
        };

        tabelaMedicos = new JTable(modeloTabela);
        tabelaMedicos.setRowHeight(40);

        tabelaMedicos.getColumn("Marcar").setCellRenderer(new ButtonRenderer());
        tabelaMedicos.getColumn("Marcar").setCellEditor(new ButtonEditor(new JCheckBox()));

        add(new JScrollPane(tabelaMedicos), BorderLayout.CENTER);
    }

    private void carregarMedicos() {
        filtrarMedicos("");
    }

    private void filtrarMedicos(String termo) {
        String t = termo.equals("Pesquisar") ? "" : termo.toLowerCase();
        try {
            medicosExibidos = medicosExibidos = t.isEmpty()
                    ? medicoRepo.listarTodos()
                    : medicoRepo.buscarPorTermo(t);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            medicosExibidos = new ArrayList<>();
        }

        modeloTabela.setRowCount(0);
        for (Medico m : medicosExibidos) {
            modeloTabela.addRow(new Object[]{
                    m.getNomeCompleto(),
                    m.getEspecialidade(),
                    m.getCrm(),
                    String.format("R$ %.2f", m.getValorConsulta()),
                    "+"
            });
        }
    }

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

    class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("+");
            button.setBackground(Color.GREEN);
            button.setForeground(Color.WHITE);
            button.addActionListener(e -> {
                fireEditingStopped();
                if (row < medicosExibidos.size()) mostrarDetalhesMedico(medicosExibidos.get(row));
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
            return "+";
        }
    }

    private void mostrarDetalhesMedico(Medico medico) {
        getContentPane().removeAll();
        add(criarCabecalho(), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JLabel lblMedico = new JLabel(medico.getNomeCompleto() + " — " + medico.getEspecialidade()
                + " (CRM " + medico.getCrm() + ")");
        lblMedico.setFont(new Font("SansSerif", Font.BOLD, 18));
        centerPanel.add(lblMedico);

        // Calendário + horário
        JComboBox<String> cbHorario = new JComboBox<>();
        CalendarioPanel calendario = new CalendarioPanel(data -> atualizarHorarios(medico, data, cbHorario));
        centerPanel.add(calendario);

        JPanel opcoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        opcoes.add(new JLabel("Horário:"));
        opcoes.add(cbHorario);

        // Atendimento definido pelo plano escolhido no cadastro do paciente
        opcoes.add(new JLabel("Atendimento: " + descreverAtendimento(medico)));
        centerPanel.add(opcoes);

        JButton btnMarcar = new JButton("Marcar Consulta");
        btnMarcar.addActionListener(e -> {
            LocalDate data = calendario.getDataSelecionada();
            if (data == null || cbHorario.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Selecione um dia e um horário disponíveis.");
                return;
            }
            LocalDateTime dataHora = LocalDateTime.of(data, LocalTime.parse((String) cbHorario.getSelectedItem(), HORA));
            if (dataHora.isBefore(LocalDateTime.now())) {
                JOptionPane.showMessageDialog(this, "Escolha um horário futuro.");
                return;
            }
            try {
                AgendamentoService service = new AgendamentoService(consultaRepo, utilizaRepo);
                if (service.agendar(medico, paciente, dataHora)) {
                    JOptionPane.showMessageDialog(this, "Consulta marcada para " + dataHora.format(FormUtil.DATA_HORA) + "!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Horário indisponível ou limite diário do médico atingido.");
                    atualizarHorarios(medico, data, cbHorario);
                }
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao agendar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        centerPanel.add(btnMarcar);

        add(centerPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private String descreverAtendimento(Medico medico) {
        try {
            Integer idPlano = utilizaRepo.existeVinculo(medico.getId(), paciente.getId())
                    ? utilizaRepo.buscarPlanoDoPacienteComMedico(medico.getId(), paciente.getId())
                    : utilizaRepo.buscarPlanoDoPaciente(paciente.getId());
            Plano plano = (idPlano == null) ? null : planoRepo.buscarPorId(idPlano);
            return (plano == null) ? String.format("Particular (R$ %.2f)", medico.getValorConsulta())
                                   : "Plano " + plano.getNomeDoPlano();
        } catch (RuntimeException e) {
            return "indisponível";
        }
    }

    private void atualizarHorarios(Medico medico, LocalDate data, JComboBox<String> cbHorario) {
        cbHorario.removeAllItems();
        if (data == null) return;
        List<LocalDateTime> ocupados;
        try {
            ocupados = consultaRepo.horariosOcupados(medico.getId(), data);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (ocupados.size() >= medico.maxConsultasDia()) return;
        for (int h = HORA_INICIO; h <= HORA_FIM; h++) {
            LocalDateTime slot = LocalDateTime.of(data, LocalTime.of(h, 0));
            if (slot.isAfter(LocalDateTime.now()) && !ocupados.contains(slot)) {
                cbHorario.addItem(slot.toLocalTime().format(HORA));
            }
        }
    }

    class CalendarioPanel extends JPanel {
        private YearMonth mesAtual;
        private ButtonGroup grupoDias;
        private final JPanel diasPanel;
        private final JLabel lblMes;
        private final LocalDate hoje;
        private final java.util.function.Consumer<LocalDate> aoSelecionar;

        public CalendarioPanel(java.util.function.Consumer<LocalDate> aoSelecionar) {
            this.aoSelecionar = aoSelecionar;
            setLayout(new BorderLayout());
            mesAtual = YearMonth.now();
            hoje = LocalDate.now();

            JPanel topo = new JPanel(new BorderLayout());
            JButton btnEsquerda = new JButton("<");
            JButton btnDireita = new JButton(">");
            lblMes = new JLabel("", SwingConstants.CENTER);
            lblMes.setFont(new Font("SansSerif", Font.BOLD, 18));

            topo.add(btnEsquerda, BorderLayout.WEST);
            topo.add(lblMes, BorderLayout.CENTER);
            topo.add(btnDireita, BorderLayout.EAST);
            add(topo, BorderLayout.NORTH);

            diasPanel = new JPanel(new GridLayout(0,7));
            add(diasPanel, BorderLayout.CENTER);

            preencherDias();

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
                rbDia.setEnabled(!data.isBefore(hoje));
                rbDia.addActionListener(e -> aoSelecionar.accept(data));
                grupoDias.add(rbDia);
                diasPanel.add(rbDia);
            }
            aoSelecionar.accept(null);
            diasPanel.revalidate();
            diasPanel.repaint();
        }

        public LocalDate getDataSelecionada() {
            for (Component c : diasPanel.getComponents()) {
                if (c instanceof JRadioButton rb && rb.isSelected()) {
                    return LocalDate.of(mesAtual.getYear(), mesAtual.getMonth(), Integer.parseInt(rb.getText()));
                }
            }
            return null;
        }
    }
}
