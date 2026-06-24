package clinica.ui;

import clinica.model.Paciente;
import clinica.exception.EspecialidadeInvalidaException;
import clinica.exception.UsuarioDuplicadoException;
import clinica.model.Medico;
import clinica.repositorio.PacienteRepository;
import clinica.service.AuthService;
import clinica.repositorio.MedicoRepository;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class CriarContaFrame extends JFrame {

    private final JPanel centerPanel = new JPanel(new GridBagLayout());
    private final JButton btnMedico = new JButton("Médico");
    private final JButton btnPaciente = new JButton("Paciente");
    private final JButton btnCancelar = new JButton("Cancelar");

    private final PacienteRepository pacienteRepo;
    private final MedicoRepository medicoRepo;

    public CriarContaFrame(PacienteRepository pacienteRepo, MedicoRepository medicoRepo) {
        super("Criar Conta - Sistema de Clínica Médica");
        this.pacienteRepo = pacienteRepo;
        this.medicoRepo = medicoRepo;
        initComponents();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JLabel lbl = new JLabel("Criar Conta", SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(lbl, BorderLayout.NORTH);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        top.add(new JLabel("Você é:"));
        top.add(btnMedico);
        top.add(btnPaciente);
        add(top, BorderLayout.PAGE_START);

        add(new JScrollPane(centerPanel), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btnCancelar);
        add(south, BorderLayout.SOUTH);

        btnMedico.addActionListener(e -> showMedicoForm());
        btnPaciente.addActionListener(e -> showPacienteForm());
        btnCancelar.addActionListener(e -> {
            new LoginClinicaFrame().setVisible(true);
            dispose();
        });
    }

    private void showMedicoForm() {
        centerPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField tfId = new JTextField();
        JPasswordField pfSenha = new JPasswordField();
        JTextField tfNome = new JTextField();
        JTextField tfEspecialidade = new JTextField();
        JTextField tfValor = new JTextField();
        JTextField tfPlanos = new JTextField();

        int row=0;
        gbc.gridx=0; gbc.gridy=row; centerPanel.add(new JLabel("Login/ID:"), gbc);
        gbc.gridx=1; centerPanel.add(tfId, gbc); row++;

        gbc.gridx=0; gbc.gridy=row; centerPanel.add(new JLabel("Senha:"), gbc);
        gbc.gridx=1; centerPanel.add(pfSenha, gbc); row++;

        gbc.gridx=0; gbc.gridy=row; centerPanel.add(new JLabel("Nome:"), gbc);
        gbc.gridx=1; centerPanel.add(tfNome, gbc); row++;

        gbc.gridx=0; gbc.gridy=row; centerPanel.add(new JLabel("Especialidade:"), gbc);
        gbc.gridx=1; centerPanel.add(tfEspecialidade, gbc); row++;

        gbc.gridx=0; gbc.gridy=row; centerPanel.add(new JLabel("Valor consulta particular:"), gbc);
        gbc.gridx=1; centerPanel.add(tfValor, gbc); row++;

        gbc.gridx=0; gbc.gridy=row; centerPanel.add(new JLabel("Planos atendidos (separados por vírgula):"), gbc);
        gbc.gridx=1; centerPanel.add(tfPlanos, gbc); row++;

        JButton btnSalvar = new JButton("Salvar Médico");
        gbc.gridx=1; gbc.gridy=row+1; centerPanel.add(btnSalvar, gbc);

        centerPanel.revalidate();
        centerPanel.repaint();

        btnSalvar.addActionListener(e -> {
            try {
                String id = tfId.getText().trim();
                String senha = new String(pfSenha.getPassword()).trim();
                String nome = tfNome.getText().trim();
                String especialidade = tfEspecialidade.getText().trim();
                double valor = Double.parseDouble(tfValor.getText().trim());
                String[] planos = tfPlanos.getText().split(",");

                if (id.isEmpty() || senha.isEmpty() || nome.isEmpty() || especialidade.isEmpty()) {
                    JOptionPane.showMessageDialog(this,"Preencha todos os campos obrigatórios.");
                    return;
                }
                AuthService criarConta = new AuthService(medicoRepo, pacienteRepo);
                criarConta.cadastrarMedico(id, nome, senha, especialidade, valor, Arrays.asList(planos));
                JOptionPane.showMessageDialog(this,"Médico cadastrado com sucesso!");
                new LoginClinicaFrame().setVisible(true);
                dispose();
            } catch (UsuarioDuplicadoException ex) {
                JOptionPane.showMessageDialog(this,"Erro: "+ex.getMessage());
            } catch (EspecialidadeInvalidaException ex){
                JOptionPane.showMessageDialog(this,"Erro: "+ex.getMessage());
            } catch (Exception ex){
                JOptionPane.showMessageDialog(this,"Erro: "+ex.getMessage());
            }
        });
    }

    private void showPacienteForm() {
        centerPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField tfId = new JTextField();
        JPasswordField pfSenha = new JPasswordField();
        JTextField tfNome = new JTextField();
        JTextField tfIdade = new JTextField();
        JTextField tfPlano = new JTextField();

        int row=0;
        gbc.gridx=0; gbc.gridy=row; centerPanel.add(new JLabel("Login/ID:"), gbc);
        gbc.gridx=1; centerPanel.add(tfId, gbc); row++;

        gbc.gridx=0; gbc.gridy=row; centerPanel.add(new JLabel("Senha:"), gbc);
        gbc.gridx=1; centerPanel.add(pfSenha, gbc); row++;

        gbc.gridx=0; gbc.gridy=row; centerPanel.add(new JLabel("Nome:"), gbc);
        gbc.gridx=1; centerPanel.add(tfNome, gbc); row++;

        gbc.gridx=0; gbc.gridy=row; centerPanel.add(new JLabel("Idade:"), gbc);
        gbc.gridx=1; centerPanel.add(tfIdade, gbc); row++;

        gbc.gridx=0; gbc.gridy=row; centerPanel.add(new JLabel("Plano de saúde (ou 'não tenho'):"), gbc);
        gbc.gridx=1; centerPanel.add(tfPlano, gbc); row++;

        JButton btnSalvar = new JButton("Salvar Paciente");
        gbc.gridx=1; gbc.gridy=row+1; centerPanel.add(btnSalvar, gbc);

        centerPanel.revalidate();
        centerPanel.repaint();

        btnSalvar.addActionListener(e -> {
            try {
                String id = tfId.getText().trim();
                String senha = new String(pfSenha.getPassword()).trim();
                String nome = tfNome.getText().trim();
                String idadeStr = tfIdade.getText().trim();
                String plano = tfPlano.getText().trim();
                if (plano.isEmpty()) plano = "não tenho";

                if (id.isEmpty() || senha.isEmpty() || nome.isEmpty()) {
                    JOptionPane.showMessageDialog(this,"Preencha todos os campos obrigatórios.");
                    return;
                }

                int idade = 0;
                if (!idadeStr.isEmpty()) {
                    idade = Integer.parseInt(idadeStr);
                }
                AuthService criarConta = new AuthService(medicoRepo, pacienteRepo);
                criarConta.cadastrarPaciente(id, nome, senha, idade, plano);
                JOptionPane.showMessageDialog(this,"Paciente cadastrado com sucesso!");
                new LoginClinicaFrame().setVisible(true);
                dispose();
            } catch (UsuarioDuplicadoException ex) {
                JOptionPane.showMessageDialog(this,"Erro: "+ex.getMessage());
            }
        });
    }
}