package clinica.ui;

import clinica.model.Avaliacao;
import clinica.model.Medico;
import clinica.model.Consulta;
import clinica.model.Conta;
import clinica.model.StatusConsulta;
import clinica.model.Notificacao;
import clinica.repositorio.MedicoRepository;
import clinica.repositorio.PacienteRepository;
import clinica.service.ConsultaService;
import clinica.repositorio.ConsultaRepository;
import clinica.repositorio.ContaRepository;
import clinica.repositorio.NotificacaoRepository;
import clinica.repositorio.AvaliacaoRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

public class RealizarConsultaFrame extends JFrame {

    private final Medico medico;
    private final MedicoRepository medicoRepo;
    private final PacienteRepository pacienteRepo;
    private ConsultaRepository consultaRepo;
    private ConsultaService consultaServico;
    private AvaliacaoRepository avaliacaoRepo;
    private ContaRepository contaRepo;
    private JTable tabelaConsultas;
    private DefaultTableModel modeloTabela;

    public RealizarConsultaFrame(Medico medico, MedicoRepository medicoRepo, PacienteRepository pacienteRepo) {
        super("Realizar Consulta");
        this.medico = medico;
        this.medicoRepo = medicoRepo;
        this.pacienteRepo = pacienteRepo;
        this.contaRepo = new ContaRepository(pacienteRepo);
        this.consultaRepo = new ConsultaRepository(medicoRepo, pacienteRepo);
        this.avaliacaoRepo = new AvaliacaoRepository(consultaRepo);
        this.consultaServico = new ConsultaService(consultaRepo, contaRepo, avaliacaoRepo);
        initComponents();
        carregarConsultas();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initHeader();

        // Tabela de consultas
        modeloTabela = new DefaultTableModel(
            new Object[]{"Paciente", "Data", "Consultar"}, 0
        );

        tabelaConsultas = new JTable(modeloTabela);
        tabelaConsultas.setRowHeight(40);

        // Renderizador e editor para o botão consultar
        tabelaConsultas.getColumn("Consultar").setCellRenderer(new ButtonRenderer());
        tabelaConsultas.getColumn("Consultar").setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(tabelaConsultas);
        add(scrollPane, BorderLayout.CENTER);

        // Botão voltar
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
        List<Consulta> consultas = consultaRepo.buscarAgendadasPorMedico(medico.getId());

        for (Consulta c : consultas) {
            modeloTabela.addRow(new Object[]{
                    c.getPaciente().getNome(),
                    c.getData().toString(),
                    "Consultar"
            });
        }
    }

    // Renderizador do botão
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("Consultar");
            setBackground(Color.BLUE);
            setForeground(Color.WHITE);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            LocalDate dataConsulta = LocalDate.parse((String) table.getValueAt(row, 1));
            if (!dataConsulta.equals(LocalDate.now())) {
                setEnabled(false);
                setBackground(Color.LIGHT_GRAY);
            } else {
                setEnabled(true);
                setBackground(Color.BLUE);
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
            button = new JButton("Consultar");
            button.setBackground(Color.BLUE);
            button.setForeground(Color.WHITE);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    realizarConsulta(row);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.row = row;
            LocalDate dataConsulta = LocalDate.parse((String) table.getValueAt(row, 1));
            if (!dataConsulta.equals(LocalDate.now())) {
                button.setEnabled(false);
                button.setBackground(Color.LIGHT_GRAY);
            } else {
                button.setEnabled(true);
                button.setBackground(Color.BLUE);
            }
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Consultar";
        }
    }

    private void realizarConsulta(int row) {
        String pacienteNome = (String) tabelaConsultas.getValueAt(row, 0);
        String dataStr = (String) tabelaConsultas.getValueAt(row, 1);

        Consulta consulta = consultaRepo.buscarAgendadasPorMedico(medico.getId()).stream()
                .filter(c -> c.getPaciente().getNome().equals(pacienteNome) && c.getData().toString().equals(dataStr))
                .findFirst().orElse(null);

        if (consulta != null && consulta.getData().equals(LocalDate.now())) {
            // Limpa o conteúdo e recria cabeçalho
            getContentPane().removeAll();
            initHeader();

            JPanel mainPanel = new JPanel(new GridLayout(1,2));

            // Painel esquerdo
            JPanel leftPanel = new JPanel();
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

            leftPanel.add(new JLabel("Médico: " + medico.getNome()));
            leftPanel.add(new JLabel("Paciente: " + consulta.getPaciente().getNome()));
            leftPanel.add(new JLabel("Data: " + consulta.getData()));

            JTextArea tfDescricao = new JTextArea(5,30);
            JTextArea tfReceita = new JTextArea(5,30);
            JTextArea tfExames = new JTextArea(5,30);

            leftPanel.add(new JLabel("Descrição:"));
            leftPanel.add(new JScrollPane(tfDescricao));
            leftPanel.add(new JLabel("Receita:"));
            leftPanel.add(new JScrollPane(tfReceita));
            leftPanel.add(new JLabel("Exames:"));
            leftPanel.add(new JScrollPane(tfExames));

            JButton btnFinalizar = new JButton("Finalizar Consulta");
            btnFinalizar.addActionListener(e -> {
                try {
                    consultaServico.realizarConsulta(consulta.getIdentificador(), tfDescricao.getText(), tfReceita.getText(), tfExames.getText());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,"Erro: "+ex.getMessage());
                }

                NotificacaoRepository notifRepo = new NotificacaoRepository();
                notifRepo.cadastrarNotificacao(
                    new Notificacao(
                        consulta.getPaciente().getId(),
                        "📢 SPAM: Sua consulta em " + consulta.getData() +
                        " com Dr(a). " + medico.getNome() +
                        " foi finalizada. Por favor, avalie sua experiência!"
                    )
                );

               
                JOptionPane.showMessageDialog(this,"Consulta finalizada!");
                dispose();
            });
            leftPanel.add(btnFinalizar);

            // Painel direito
            JPanel rightPanel = new JPanel(new BorderLayout());
            JButton btnVerProntuario = new JButton("Ver Prontuário");
            rightPanel.add(btnVerProntuario, BorderLayout.CENTER);

            btnVerProntuario.addActionListener(e -> {
                rightPanel.removeAll();

                String[] colunas = {"Data","Médico","Diagnóstico","Medicamentos","Exames","Valor"};
                DefaultTableModel modelo = new DefaultTableModel(colunas,0);
                JTable tabelaProntuario = new JTable(modelo);

                for (Consulta c : consultaRepo.buscarPorPaciente(consulta.getPaciente().getId())){
                    if (c.getStatus().equals(StatusConsulta.REALIZADA)){
                        modelo.addRow(new Object[]{
                            c.getData(),
                            c.getMedico().getNome(),
                            c.getDescricao(),
                            c.getReceita(),
                            c.getExames(),
                            c.getValorPago()
                        });
                    }
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

            // Botão voltar
            JButton btnVoltar = new JButton("Voltar");
            btnVoltar.addActionListener(e -> dispose());
            add(btnVoltar, BorderLayout.SOUTH);

            revalidate();
            repaint();
        }
    }
}
