package clinica.ui;

import clinica.model.Paciente;
import clinica.model.Consulta;
import clinica.model.Medico;
import clinica.model.Avaliacao;
import clinica.repositorio.PacienteRepository;
import clinica.repositorio.MedicoRepository;
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
import java.util.List;

public class CancelarAgendamentoFrame extends JFrame {

    private final Paciente paciente;
    private final PacienteRepository pacienteRepo;
    private final MedicoRepository medicoRepo;
    private ConsultaRepository consultaRepo;
    private ListaEsperaRepository listaRepo;
    private JTable tabelaConsultas;
    private DefaultTableModel modeloTabela;

    public CancelarAgendamentoFrame(Paciente paciente, PacienteRepository pacienteRepo, MedicoRepository medicoRepo) {
        super("Cancelar Agendamento");
        this.paciente = paciente;
        this.pacienteRepo = pacienteRepo;
        this.medicoRepo = medicoRepo;
        this.consultaRepo = new ConsultaRepository(medicoRepo, pacienteRepo);
        this.listaRepo = new ListaEsperaRepository(medicoRepo, pacienteRepo);
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
            new Object[]{"Médico", "Especialidade", "Data", "Ação"}, 0
        );

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
        List<Consulta> consultas = consultaRepo.buscarAgendadas(paciente.getId());

        for (Consulta c : consultas) {
            modeloTabela.addRow(new Object[]{
                    c.getMedico().getNome(),
                    c.getMedico().getEspecialidade(),
                    c.getData().toString(),
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
        private JButton button;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("-");
            button.setBackground(Color.RED);
            button.setForeground(Color.WHITE);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    cancelarConsulta(row);
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
            return "-";
        }
    }

    private void cancelarConsulta(int row) {
        String medicoNome = (String) tabelaConsultas.getValueAt(row, 0);
        String dataStr = (String) tabelaConsultas.getValueAt(row, 2);

        // Identificador da consulta é composto internamente, aqui simplificamos
        Consulta consulta = consultaRepo.buscarAgendadas(paciente.getId()).stream()
                .filter(c -> c.getMedico().getNome().equals(medicoNome) && c.getData().toString().equals(dataStr))
                .findFirst().orElse(null);

        if (consulta != null) {
            AgendamentoService service = new AgendamentoService(consultaRepo, listaRepo);
            boolean sucesso = service.cancelar(consulta.getIdentificador());
            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Consulta cancelada com sucesso!");
                carregarConsultas();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao cancelar consulta.");
            }
        }
    }
}