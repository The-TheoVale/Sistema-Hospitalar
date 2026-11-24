package clinica.ui;

import clinica.exception.LoginInvalidoException;
import clinica.model.Medico;
import clinica.model.Paciente;
import clinica.model.Usuario;
import clinica.repositorio.ConsultaRepository;
import clinica.repositorio.MedicoRepository;
import clinica.repositorio.PacienteRepository;
import clinica.service.AuthService;

import javax.swing.*;
import java.awt.*;

public class LoginClinicaFrame extends JFrame {

    private final JTextField tfLogin = new JTextField();
    private final JPasswordField pfSenha = new JPasswordField();
    private final JButton btnEntrar = new JButton("Entrar");
    private final JButton btnCriarConta = new JButton("Criar Conta");

    private final PacienteRepository pacienteRepo = new PacienteRepository();
    private final MedicoRepository medicoRepo = new MedicoRepository();
    private final ConsultaRepository consultaRepo = new ConsultaRepository(medicoRepo, pacienteRepo);

    public LoginClinicaFrame() {
        super("Login - Sistema de Clínica Médica");
        initComponents();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10,10));

        JLabel lblTitulo = new JLabel("Sistema de Clínica Médica", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 40));
        add(lblTitulo, BorderLayout.NORTH);

        // Painel central com GridBagLayout
        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.NONE; // não expandir
        gbc.anchor = GridBagConstraints.CENTER; // centralizar

        // Definindo tamanho fixo para os campos
        tfLogin.setPreferredSize(new Dimension(200, 30));
        pfSenha.setPreferredSize(new Dimension(200, 30));

        gbc.gridx=0; gbc.gridy=0;
        center.add(new JLabel("E-mail:"), gbc);
        gbc.gridx=1; gbc.gridy=0;
        center.add(tfLogin, gbc);

        gbc.gridx=0; gbc.gridy=1;
        center.add(new JLabel("Senha:"), gbc);
        gbc.gridx=1; gbc.gridy=1;
        center.add(pfSenha, gbc);

        add(center, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        south.add(btnCriarConta);
        south.add(btnEntrar);
        add(south, BorderLayout.SOUTH);

        btnEntrar.addActionListener(e -> autenticar());
        btnCriarConta.addActionListener(e -> {
            new CriarContaFrame(pacienteRepo, medicoRepo).setVisible(true);
            dispose();
        });
    }

    private void autenticar() {
        String login = tfLogin.getText().trim();
        String senha = new String(pfSenha.getPassword()).trim();

        try {
            Usuario usuario = new AuthService(medicoRepo, pacienteRepo).login(login, senha);
            if (usuario instanceof Paciente p) {
                JOptionPane.showMessageDialog(this,"Login de paciente realizado com sucesso!");
                new PacienteMenuFrame(p, pacienteRepo, medicoRepo, consultaRepo).setVisible(true);
            } else if (usuario instanceof Medico m) {
                JOptionPane.showMessageDialog(this,"Login de médico realizado com sucesso!");
                new MedicoMenuFrame(m, pacienteRepo, medicoRepo, consultaRepo).setVisible(true);
            }
            dispose();
        } catch (LoginInvalidoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao acessar o banco: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
