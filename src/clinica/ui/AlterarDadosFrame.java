package clinica.ui;

import clinica.model.Paciente;
import clinica.model.Medico;
import clinica.repositorio.PacienteRepository;
import clinica.repositorio.MedicoRepository;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class AlterarDadosFrame extends JFrame {

    private final Paciente paciente;
    private final Medico medico;
    private final PacienteRepository pacienteRepo;
    private final MedicoRepository medicoRepo;

    public AlterarDadosFrame(Paciente paciente, PacienteRepository pacienteRepo) {
        super("Alterar Dados - Paciente");
        this.paciente = paciente;
        this.medico = null;
        this.pacienteRepo = pacienteRepo;
        this.medicoRepo = null;
        initComponents();
    }

    public AlterarDadosFrame(Medico medico, MedicoRepository medicoRepo) {
        super("Alterar Dados - Médico");
        this.medico = medico;
        this.paciente = null;
        this.medicoRepo = medicoRepo;
        this.pacienteRepo = null;
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
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

        // Painel central com campos editáveis
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField tfId = new JTextField();
        tfId.setEditable(false); // ID não deve ser alterado
        JTextField tfNome = new JTextField();
        JPasswordField pfSenha = new JPasswordField();

        int row=0;
        gbc.gridx=0; gbc.gridy=row; centerPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx=1; centerPanel.add(tfId, gbc); row++;

        gbc.gridx=0; gbc.gridy=row; centerPanel.add(new JLabel("Nome:"), gbc);
        gbc.gridx=1; centerPanel.add(tfNome, gbc); row++;

        gbc.gridx=0; gbc.gridy=row; centerPanel.add(new JLabel("Senha:"), gbc);
        gbc.gridx=1; centerPanel.add(pfSenha, gbc); row++;

        if (paciente != null) {
            JTextField tfIdade = new JTextField();
            JTextField tfPlano = new JTextField();

            gbc.gridx=0; gbc.gridy=row; centerPanel.add(new JLabel("Idade:"), gbc);
            gbc.gridx=1; centerPanel.add(tfIdade, gbc); row++;

            gbc.gridx=0; gbc.gridy=row; centerPanel.add(new JLabel("Plano de Saúde:"), gbc);
            gbc.gridx=1; centerPanel.add(tfPlano, gbc); row++;

            // Preenche com dados atuais
            tfId.setText(paciente.getId());
            tfNome.setText(paciente.getNome());
            pfSenha.setText(paciente.getSenha());
            tfIdade.setText(String.valueOf(paciente.getIdade()));
            tfPlano.setText(paciente.getPlanoSaude());

            JButton btnSalvar = new JButton("Salvar Alterações");
            gbc.gridx=1; gbc.gridy=row+1; centerPanel.add(btnSalvar, gbc);

            btnSalvar.addActionListener(e -> {
                paciente.setNome(tfNome.getText().trim());
                paciente.setSenha(new String(pfSenha.getPassword()).trim());
                paciente.setIdade(Integer.parseInt(tfIdade.getText().trim()));
                paciente.setPlano(tfPlano.getText().trim());
                try {
                    pacienteRepo.salvar(); // regrava CSV
                    JOptionPane.showMessageDialog(this,"Dados do paciente atualizados!");
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,"Erro: "+ex.getMessage());
                }
            });
        }

        if (medico != null) {
            JTextField tfEspecialidade = new JTextField();
            JTextField tfValor = new JTextField();
            JTextField tfPlanos = new JTextField();

            gbc.gridx=0; gbc.gridy=row; centerPanel.add(new JLabel("Especialidade:"), gbc);
            gbc.gridx=1; centerPanel.add(tfEspecialidade, gbc); row++;

            gbc.gridx=0; gbc.gridy=row; centerPanel.add(new JLabel("Valor Consulta:"), gbc);
            gbc.gridx=1; centerPanel.add(tfValor, gbc); row++;

            gbc.gridx=0; gbc.gridy=row; centerPanel.add(new JLabel("Planos Atendidos:"), gbc);
            gbc.gridx=1; centerPanel.add(tfPlanos, gbc); row++;

            // Preenche com dados atuais
            tfId.setText(medico.getId());
            tfNome.setText(medico.getNome());
            pfSenha.setText(medico.getSenha());
            tfEspecialidade.setText(medico.getEspecialidade());
            tfValor.setText(String.valueOf(medico.getValorConsultaParticular()));
            tfPlanos.setText(String.join(",", medico.getPlanosAtendidos()));

            JButton btnSalvar = new JButton("Salvar Alterações");
            gbc.gridx=1; gbc.gridy=row+1; centerPanel.add(btnSalvar, gbc);

            btnSalvar.addActionListener(e -> {
                medico.setNome(tfNome.getText().trim());
                medico.setSenha(new String(pfSenha.getPassword()).trim());
                medico.setEspecialidade(tfEspecialidade.getText().trim());
                medico.setValorConsultaParticular(Double.parseDouble(tfValor.getText().trim()));
                medico.setPlanosAtendidos(Arrays.asList(tfPlanos.getText().split(",")));
                // especialidade, valor e planos poderiam ser atualizados aqui também
                try {
                    medicoRepo.salvar();
                    JOptionPane.showMessageDialog(this,"Dados do médico atualizados!");
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,"Erro: "+ex.getMessage());
                }
            });
        }

        add(centerPanel, BorderLayout.CENTER);
    }
}