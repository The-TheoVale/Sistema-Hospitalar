package clinica.ui;

import clinica.model.Medico;
import clinica.repositorio.*;

import javax.swing.*;
import java.awt.*;

public class MedicoMenuFrame extends JFrame {
    private final Medico medico;
    private final MedicoRepository medicoRepo;
    private final PacienteRepository pacienteRepo;
    private final ConsultaRepository consultaRepo;

    public MedicoMenuFrame(Medico medico, PacienteRepository pacienteRepo, MedicoRepository medicoRepo, ConsultaRepository consultaRepo) {
        super("Menu do Médico");
        this.medico = medico;
        this.medicoRepo = medicoRepo;
        this.pacienteRepo = pacienteRepo;
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
        JButton btnRealizar = new JButton("Realizar Consulta");
        btnRealizar.setBackground(Color.GREEN);
        btnRealizar.addActionListener(e -> new RealizarConsultaFrame(medico, pacienteRepo, consultaRepo).setVisible(true));



        JButton btnAlterar = new JButton("Alterar Dados");
        btnAlterar.setBackground(Color.BLUE);
        btnAlterar.setForeground(Color.WHITE);
        btnAlterar.addActionListener(e -> new AlterarDadosFrame(medico, medicoRepo).setVisible(true));

        JButton btnLogout = new JButton("Sair");
        btnLogout.setBackground(Color.GRAY);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.addActionListener(e -> {
            new LoginClinicaFrame().setVisible(true);
            dispose();
        });

        buttonPanel.add(btnRealizar);
        buttonPanel.add(btnAlterar);
        buttonPanel.add(btnLogout);
        
        northPanel.add(lblTitulo);
        northPanel.add(separator);
        northPanel.add(buttonPanel);

        add(northPanel, BorderLayout.NORTH);

        // Espaço central
        add(new EstatisticasPanel(consultaRepo), BorderLayout.CENTER);

    }
}
