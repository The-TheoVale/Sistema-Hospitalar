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
    private final AvaliacaoRepository avaliacaoRepo;

    public MedicoMenuFrame(Medico medico, PacienteRepository pacienteRepo, MedicoRepository medicoRepo, ConsultaRepository consultaRepo, AvaliacaoRepository avaliacaoRepo) {
        super("Menu do Médico");
        this.medico = medico;
        this.medicoRepo = medicoRepo;
        this.pacienteRepo = pacienteRepo;
        this.consultaRepo = consultaRepo;
        this.avaliacaoRepo = avaliacaoRepo;
        
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
        btnRealizar.addActionListener(e -> new RealizarConsultaFrame(medico, medicoRepo, pacienteRepo).setVisible(true));


        JButton btnAvaliacoes = new JButton("Avaliações");
        btnAvaliacoes.setBackground(Color.WHITE);
        btnAvaliacoes.addActionListener(e -> new AvaliacoesFrame(medico, medicoRepo, pacienteRepo).setVisible(true));

        JButton btnAlterar = new JButton("Alterar Dados");
        btnAlterar.setBackground(Color.BLUE);
        btnAlterar.setForeground(Color.WHITE);
        btnAlterar.addActionListener(e -> new AlterarDadosFrame(medico, medicoRepo).setVisible(true));

        buttonPanel.add(btnRealizar);
        buttonPanel.add(btnAvaliacoes);
        buttonPanel.add(btnAlterar);

        northPanel.add(lblTitulo);
        northPanel.add(separator);
        northPanel.add(buttonPanel);

        add(northPanel, BorderLayout.NORTH);

        // Espaço central
        add(new EstatisticasPanel(medicoRepo, consultaRepo), BorderLayout.CENTER);

    }
}
