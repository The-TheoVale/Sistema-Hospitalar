package clinica.ui;

import clinica.exception.EspecialidadeInvalidaException;
import clinica.exception.UsuarioDuplicadoException;
import clinica.model.Plano;
import clinica.repositorio.MedicoRepository;
import clinica.repositorio.PacienteRepository;
import clinica.repositorio.PlanoRepository;
import clinica.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class CriarContaFrame extends JFrame {

    private static final String[] ESPECIALIDADES = {"Clínico Geral", "Cardiologista", "Pediatra", "Cirurgião"};

    private final JPanel centerPanel = new JPanel(new GridBagLayout());
    private final JButton btnMedico = new JButton("Médico");
    private final JButton btnPaciente = new JButton("Paciente");
    private final JButton btnCancelar = new JButton("Cancelar");

    private final PacienteRepository pacienteRepo;
    private final MedicoRepository medicoRepo;

    // Campos comuns
    private JTextField tfNome, tfSobrenome, tfEmail, tfDataNasc;
    private JPasswordField pfSenha;
    private JComboBox<String> cbSexo;
    private int row;

    public CriarContaFrame(PacienteRepository pacienteRepo, MedicoRepository medicoRepo) {
        super("Criar Conta - Sistema de Clínica Médica");
        this.pacienteRepo = pacienteRepo;
        this.medicoRepo = medicoRepo;
        initComponents();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JLabel lbl = new JLabel("Criar Conta", SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 22));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        top.add(new JLabel("Você é:"));
        top.add(btnMedico);
        top.add(btnPaciente);

        JPanel north = new JPanel(new BorderLayout());
        north.add(lbl, BorderLayout.NORTH);
        north.add(top, BorderLayout.CENTER);
        add(north, BorderLayout.NORTH);

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

    private GridBagConstraints iniciarFormulario() {
        centerPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,8,6,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        row = 0;

        tfNome = new JTextField(20);
        tfSobrenome = new JTextField(20);
        tfEmail = new JTextField(20);
        pfSenha = new JPasswordField(20);
        tfDataNasc = new JTextField(20);
        cbSexo = new JComboBox<>(FormUtil.SEXOS);

        adicionarCampo(gbc, "Nome*:", tfNome);
        adicionarCampo(gbc, "Sobrenome:", tfSobrenome);
        adicionarCampo(gbc, "E-mail* (login):", tfEmail);
        adicionarCampo(gbc, "Senha*:", pfSenha);
        adicionarCampo(gbc, "Data de nascimento* (dd/mm/aaaa):", tfDataNasc);
        adicionarCampo(gbc, "Sexo (M/F/O):", cbSexo);
        return gbc;
    }

    private void adicionarCampo(GridBagConstraints gbc, String rotulo, JComponent campo) {
        gbc.gridx=0; gbc.gridy=row; centerPanel.add(new JLabel(rotulo), gbc);
        gbc.gridx=1; centerPanel.add(campo, gbc); row++;
    }

    private void finalizarFormulario(GridBagConstraints gbc, JButton btnSalvar) {
        gbc.gridx=1; gbc.gridy=row+1; centerPanel.add(btnSalvar, gbc);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    /** Valida os campos comuns e devolve a data de nascimento. */
    private LocalDate validarComuns() {
        FormUtil.exigir(!tfNome.getText().isBlank(), "Informe o nome.");
        FormUtil.validarEmail(tfEmail.getText().trim());
        FormUtil.exigir(pfSenha.getPassword().length > 0, "Informe a senha.");
        LocalDate nasc = FormUtil.parseData(tfDataNasc.getText(), "Data de nascimento");
        FormUtil.exigir(!nasc.isAfter(LocalDate.now()), "Data de nascimento não pode ser futura.");
        return nasc;
    }

    private void showMedicoForm() {
        GridBagConstraints gbc = iniciarFormulario();

        JTextField tfCrm = new JTextField(20);
        JTextField tfValor = new JTextField(20);
        JTextField tfTelefone = new JTextField(20);
        JComboBox<String> cbEspecialidade = new JComboBox<>(ESPECIALIDADES);

        // Campos específicos por especialidade
        JCheckBox chkEco = new JCheckBox("Realiza ecocardiograma");
        JTextField tfFaixaEtaria = new JTextField(20);
        JCheckBox chkRecemNascidos = new JCheckBox("Atende recém-nascidos");
        JTextField tfAreaCirurgica = new JTextField(20);
        JTextField tfTempoExp = new JTextField(20);

        adicionarCampo(gbc, "CRM*:", tfCrm);
        adicionarCampo(gbc, "Valor da consulta* (R$):", tfValor);
        adicionarCampo(gbc, "Telefone profissional:", tfTelefone);
        adicionarCampo(gbc, "Especialidade*:", cbEspecialidade);

        JPanel painelEspecifico = new JPanel(new GridBagLayout());
        gbc.gridx=0; gbc.gridy=row; gbc.gridwidth=2; centerPanel.add(painelEspecifico, gbc);
        gbc.gridwidth=1; row++;

        Runnable atualizarEspecifico = () -> {
            painelEspecifico.removeAll();
            GridBagConstraints g = new GridBagConstraints();
            g.insets = new Insets(4,8,4,8);
            g.fill = GridBagConstraints.HORIZONTAL;
            switch ((String) cbEspecialidade.getSelectedItem()) {
                case "Cardiologista" -> { g.gridx=1; g.gridy=0; painelEspecifico.add(chkEco, g); }
                case "Pediatra" -> {
                    g.gridx=0; g.gridy=0; painelEspecifico.add(new JLabel("Faixa etária atendida*:"), g);
                    g.gridx=1; painelEspecifico.add(tfFaixaEtaria, g);
                    g.gridx=1; g.gridy=1; painelEspecifico.add(chkRecemNascidos, g);
                }
                case "Cirurgião" -> {
                    g.gridx=0; g.gridy=0; painelEspecifico.add(new JLabel("Área cirúrgica*:"), g);
                    g.gridx=1; painelEspecifico.add(tfAreaCirurgica, g);
                    g.gridx=0; g.gridy=1; painelEspecifico.add(new JLabel("Tempo de experiência (anos):"), g);
                    g.gridx=1; painelEspecifico.add(tfTempoExp, g);
                }
                default -> { }
            }
            painelEspecifico.revalidate();
            painelEspecifico.repaint();
        };
        cbEspecialidade.addActionListener(e -> atualizarEspecifico.run());
        atualizarEspecifico.run();

        JButton btnSalvar = new JButton("Salvar Médico");
        finalizarFormulario(gbc, btnSalvar);

        btnSalvar.addActionListener(e -> {
            try {
                LocalDate nasc = validarComuns();
                String crm = tfCrm.getText().trim();
                FormUtil.exigir(!crm.isEmpty(), "Informe o CRM.");
                double valor;
                try {
                    valor = Double.parseDouble(tfValor.getText().trim().replace(",", "."));
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Valor da consulta inválido.");
                }
                FormUtil.exigir(valor > 0, "O valor da consulta deve ser maior que zero.");

                String especialidade = (String) cbEspecialidade.getSelectedItem();
                String faixa = tfFaixaEtaria.getText().trim();
                String area = tfAreaCirurgica.getText().trim();
                short tempo = 0;
                if ("Pediatra".equals(especialidade)) FormUtil.exigir(!faixa.isEmpty(), "Informe a faixa etária atendida.");
                if ("Cirurgião".equals(especialidade)) {
                    FormUtil.exigir(!area.isEmpty(), "Informe a área cirúrgica.");
                    if (!tfTempoExp.getText().isBlank()) {
                        try {
                            tempo = Short.parseShort(tfTempoExp.getText().trim());
                        } catch (NumberFormatException ex) {
                            throw new IllegalArgumentException("Tempo de experiência inválido.");
                        }
                        FormUtil.exigir(tempo >= 0 && tempo <= 70, "Tempo de experiência deve estar entre 0 e 70 anos.");
                    }
                }

                new AuthService(medicoRepo, pacienteRepo).cadastrarMedico(
                        tfNome.getText().trim(), FormUtil.vazioParaNull(tfSobrenome.getText()),
                        tfEmail.getText().trim(), new String(pfSenha.getPassword()).trim(),
                        nasc, ((String) cbSexo.getSelectedItem()).charAt(0), crm, valor,
                        FormUtil.vazioParaNull(tfTelefone.getText()), especialidade,
                        chkEco.isSelected(), faixa, chkRecemNascidos.isSelected(), area, tempo);
                JOptionPane.showMessageDialog(this,"Médico cadastrado com sucesso!");
                new LoginClinicaFrame().setVisible(true);
                dispose();
            } catch (UsuarioDuplicadoException | EspecialidadeInvalidaException | RuntimeException ex) {
                JOptionPane.showMessageDialog(this,"Erro: "+ex.getMessage());
            }
        });
    }

    private void showPacienteForm() {
        GridBagConstraints gbc = iniciarFormulario();

        JTextField tfCpf = new JTextField(20);
        JTextField tfEndereco = new JTextField(20);
        JTextField tfTelefone = new JTextField(20);

        adicionarCampo(gbc, "CPF* (11 dígitos):", tfCpf);
        adicionarCampo(gbc, "Endereço:", tfEndereco);
        adicionarCampo(gbc, "Telefone:", tfTelefone);

        JComboBox<Object> cbPlano = new JComboBox<>();
        cbPlano.addItem("Particular");
        try {
            for (Plano p : new PlanoRepository().listarTodos()) cbPlano.addItem(p);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Não foi possível carregar os planos: " + ex.getMessage());
        }
        adicionarCampo(gbc, "Plano de saúde:", cbPlano);

        JButton btnSalvar = new JButton("Salvar Paciente");
        finalizarFormulario(gbc, btnSalvar);

        btnSalvar.addActionListener(e -> {
            try {
                LocalDate nasc = validarComuns();
                String cpf = FormUtil.somenteDigitos(tfCpf.getText());
                FormUtil.exigir(cpf.length() == 11, "CPF deve conter 11 dígitos.");

                new AuthService(medicoRepo, pacienteRepo).cadastrarPaciente(
                        tfNome.getText().trim(), FormUtil.vazioParaNull(tfSobrenome.getText()),
                        tfEmail.getText().trim(), new String(pfSenha.getPassword()).trim(),
                        nasc, ((String) cbSexo.getSelectedItem()).charAt(0), cpf,
                        FormUtil.vazioParaNull(tfEndereco.getText()), FormUtil.vazioParaNull(tfTelefone.getText()),
                        (cbPlano.getSelectedItem() instanceof Plano p) ? p.getIdPlano() : null);
                JOptionPane.showMessageDialog(this,"Paciente cadastrado com sucesso!");
                new LoginClinicaFrame().setVisible(true);
                dispose();
            } catch (UsuarioDuplicadoException | RuntimeException ex) {
                JOptionPane.showMessageDialog(this,"Erro: "+ex.getMessage());
            }
        });
    }
}
