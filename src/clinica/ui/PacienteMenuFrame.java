package clinica.ui;

import clinica.model.Paciente;
import clinica.repositorio.*;
import javax.swing.*;
import java.awt.*;

public class PacienteMenuFrame extends JFrame {
    private final Paciente paciente;
    private final PacienteRepository pacienteRepo;
    private final MedicoRepository medicoRepo;
    private final ConsultaRepository consultaRepo;
    
    public PacienteMenuFrame(Paciente paciente, PacienteRepository pacienteRepo, MedicoRepository medicoRepo, ConsultaRepository consultaRepo) {
        super("Menu do Paciente");
        this.paciente = paciente;
        this.pacienteRepo = pacienteRepo;
        this.medicoRepo = medicoRepo;
        this.consultaRepo = consultaRepo;
        
        initComponents();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
    setLayout(new BorderLayout());

    // Cabeçalho
    JPanel northPanel = new JPanel();
    northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));

    JLabel lblTitulo = new JLabel("Sistema de Clínica Médica", SwingConstants.CENTER);
    lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 40));
    lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

    JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
    separator.setForeground(Color.BLACK);
    separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
    JButton btnAgendar = new JButton("Agendar Consulta");
    btnAgendar.setBackground(Color.GREEN);
    btnAgendar.addActionListener(e -> new AgendarConsultaFrame(paciente, medicoRepo, consultaRepo).setVisible(true));

    JButton btnCancelar = new JButton("Cancelar Agendamento");
    btnCancelar.setBackground(Color.RED);
    btnCancelar.setForeground(Color.WHITE);
    btnCancelar.addActionListener(e -> new CancelarAgendamentoFrame(paciente, consultaRepo).setVisible(true));

    JButton btnAlterar = new JButton("Alterar Dados");
    btnAlterar.setBackground(Color.BLUE);
    btnAlterar.setForeground(Color.WHITE);
    btnAlterar.addActionListener(e -> new AlterarDadosFrame(paciente, pacienteRepo).setVisible(true));

    // Novo botão Pagar
    JButton btnPagar = new JButton("Pagar");
    btnPagar.setBackground(Color.ORANGE);
    btnPagar.setForeground(Color.BLACK);
    btnPagar.addActionListener(e -> new PagamentoFrame(paciente, pacienteRepo).setVisible(true));

    JButton btnLogout = new JButton("Sair");
    btnLogout.setBackground(Color.GRAY);
    btnLogout.setForeground(Color.WHITE);
    btnLogout.addActionListener(e -> {
        new LoginClinicaFrame().setVisible(true);
        dispose();
    });

    // Adiciona todos os botões ao painel
    buttonPanel.add(btnAgendar);
    buttonPanel.add(btnCancelar);
    buttonPanel.add(btnAlterar);
    buttonPanel.add(btnPagar);
    buttonPanel.add(btnLogout);

    northPanel.add(lblTitulo);
    northPanel.add(separator);
    northPanel.add(buttonPanel);

    add(northPanel, BorderLayout.NORTH);

    // Espaço central
    add(new EstatisticasPanel(consultaRepo), BorderLayout.CENTER);

    } 

    
}