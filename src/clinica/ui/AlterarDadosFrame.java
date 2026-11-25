package clinica.ui;

import clinica.model.Cardiologista;
import clinica.model.Cirurgiao;
import clinica.model.Medico;
import clinica.model.Paciente;
import clinica.model.Pediatra;
import clinica.model.Usuario;
import clinica.repositorio.MedicoRepository;
import clinica.repositorio.PacienteRepository;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class AlterarDadosFrame extends JFrame {

    private final Paciente paciente;
    private final Medico medico;
    private final PacienteRepository pacienteRepo;
    private final MedicoRepository medicoRepo;

    private final JPanel centerPanel = new JPanel(new GridBagLayout());
    private final GridBagConstraints gbc = new GridBagConstraints();
    private int row = 0;

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

    private void adicionarCampo(String rotulo, JComponent campo) {
        gbc.gridx=0; gbc.gridy=row; centerPanel.add(new JLabel(rotulo), gbc);
        gbc.gridx=1; centerPanel.add(campo, gbc); row++;
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(650, 650);
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

        gbc.insets = new Insets(6,8,6,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Usuario usuario = (paciente != null) ? paciente : medico;

        JTextField tfId = new JTextField(String.valueOf(usuario.getId()), 20);
        tfId.setEditable(false); // ID não deve ser alterado
        JTextField tfNome = new JTextField(usuario.getNome(), 20);
        JTextField tfSobrenome = new JTextField(usuario.getSobrenome(), 20);
        JTextField tfEmail = new JTextField(usuario.getEmail(), 20);
        JPasswordField pfSenha = new JPasswordField(usuario.getSenha(), 20);
        JTextField tfDataNasc = new JTextField(FormUtil.formatar(usuario.getDataNascimento()), 20);
        JComboBox<String> cbSexo = new JComboBox<>(FormUtil.SEXOS);
        cbSexo.setSelectedItem(String.valueOf(usuario.getSexo()));

        adicionarCampo("ID:", tfId);
        adicionarCampo("Nome*:", tfNome);
        adicionarCampo("Sobrenome:", tfSobrenome);
        adicionarCampo("E-mail*:", tfEmail);
        adicionarCampo("Senha*:", pfSenha);
        adicionarCampo("Data de nascimento* (dd/mm/aaaa):", tfDataNasc);
        adicionarCampo("Sexo:", cbSexo);

        Runnable aplicarComuns = () -> {
            FormUtil.exigir(!tfNome.getText().isBlank(), "Informe o nome.");
            FormUtil.validarEmail(tfEmail.getText().trim());
            FormUtil.exigir(pfSenha.getPassword().length > 0, "Informe a senha.");
            LocalDate nasc = FormUtil.parseData(tfDataNasc.getText(), "Data de nascimento");
            FormUtil.exigir(!nasc.isAfter(LocalDate.now()), "Data de nascimento não pode ser futura.");
            usuario.setNome(tfNome.getText().trim());
            usuario.setSobrenome(FormUtil.vazioParaNull(tfSobrenome.getText()));
            usuario.setEmail(tfEmail.getText().trim());
            usuario.setSenha(new String(pfSenha.getPassword()).trim());
            usuario.setDataNascimento(nasc);
            usuario.setSexo(((String) cbSexo.getSelectedItem()).charAt(0));
        };

        JButton btnSalvar = new JButton("Salvar Alterações");

        if (paciente != null) {
            JTextField tfCpf = new JTextField(paciente.getCpf(), 20);
            JTextField tfEndereco = new JTextField(paciente.getEndereco(), 20);
            JTextField tfTelefone = new JTextField(paciente.getTelefone(), 20);

            adicionarCampo("CPF*:", tfCpf);
            adicionarCampo("Endereço:", tfEndereco);
            adicionarCampo("Telefone:", tfTelefone);

            btnSalvar.addActionListener(e -> {
                try {
                    String cpf = FormUtil.somenteDigitos(tfCpf.getText());
                    FormUtil.exigir(cpf.length() == 11, "CPF deve conter 11 dígitos.");
                    aplicarComuns.run();
                    paciente.setCpf(cpf);
                    paciente.setEndereco(FormUtil.vazioParaNull(tfEndereco.getText()));
                    paciente.setTelefone(FormUtil.vazioParaNull(tfTelefone.getText()));
                    pacienteRepo.atualizar(paciente);
                    JOptionPane.showMessageDialog(this,"Dados do paciente atualizados!");
                    dispose();
                } catch (IllegalStateException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(),
                            "Erro de integridade (banco)", JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {

                }
            });
        }

        if (medico != null) {
            JTextField tfEspecialidade = new JTextField(medico.getEspecialidade(), 20);
            tfEspecialidade.setEditable(false);
            JTextField tfCrm = new JTextField(medico.getCrm(), 20);
            JTextField tfValor = new JTextField(String.valueOf(medico.getValorDaConsulta()), 20);
            JTextField tfTelefone = new JTextField(medico.getTelefoneProfissional(), 20);

            adicionarCampo("Especialidade:", tfEspecialidade);
            adicionarCampo("CRM*:", tfCrm);
            adicionarCampo("Valor da consulta* (R$):", tfValor);
            adicionarCampo("Telefone profissional:", tfTelefone);

            JCheckBox chkEco = new JCheckBox("Realiza ecocardiograma");
            JTextField tfFaixa = new JTextField(20);
            JCheckBox chkRecem = new JCheckBox("Atende recém-nascidos");
            JTextField tfArea = new JTextField(20);
            JTextField tfTempo = new JTextField(20);

            if (medico instanceof Cardiologista c) {
                chkEco.setSelected(c.isRealizaEcocardiograma());
                adicionarCampo("", chkEco);
            } else if (medico instanceof Pediatra p) {
                tfFaixa.setText(p.getFaixaEtariaAtendida());
                chkRecem.setSelected(p.isAtendeRecemNascidos());
                adicionarCampo("Faixa etária atendida*:", tfFaixa);
                adicionarCampo("", chkRecem);
            } else if (medico instanceof Cirurgiao ci) {
                tfArea.setText(ci.getAreaCirurgica());
                tfTempo.setText(String.valueOf(ci.getTempoExperiencia()));
                adicionarCampo("Área cirúrgica*:", tfArea);
                adicionarCampo("Tempo de experiência (anos):", tfTempo);
            }

            btnSalvar.addActionListener(e -> {
                try {
                    FormUtil.exigir(!tfCrm.getText().isBlank(), "Informe o CRM.");
                    double valor;
                    try {
                        valor = Double.parseDouble(tfValor.getText().trim().replace(",", "."));
                    } catch (NumberFormatException ex) {
                        throw new IllegalArgumentException("Valor da consulta inválido.");
                    }
                    FormUtil.exigir(valor > 0, "O valor da consulta deve ser maior que zero.");
                    if (medico instanceof Pediatra) FormUtil.exigir(!tfFaixa.getText().isBlank(), "Informe a faixa etária.");
                    short tempo = 0;
                    if (medico instanceof Cirurgiao) {
                        FormUtil.exigir(!tfArea.getText().isBlank(), "Informe a área cirúrgica.");
                        try {
                            tempo = tfTempo.getText().isBlank() ? 0 : Short.parseShort(tfTempo.getText().trim());
                        } catch (NumberFormatException ex) {
                            throw new IllegalArgumentException("Tempo de experiência inválido.");
                        }
                        FormUtil.exigir(tempo >= 0 && tempo <= 70, "Tempo de experiência deve estar entre 0 e 70 anos.");
                    }

                    aplicarComuns.run();
                    medico.setCrm(tfCrm.getText().trim());
                    medico.setValorDaConsulta(valor);
                    medico.setTelefoneProfissional(FormUtil.vazioParaNull(tfTelefone.getText()));
                    if (medico instanceof Cardiologista c) c.setRealizaEcocardiograma(chkEco.isSelected());
                    if (medico instanceof Pediatra p) {
                        p.setFaixaEtariaAtendida(tfFaixa.getText().trim());
                        p.setAtendeRecemNascidos(chkRecem.isSelected());
                    }
                    if (medico instanceof Cirurgiao ci) {
                        ci.setAreaCirurgica(tfArea.getText().trim());
                        ci.setTempoExperiencia(tempo);
                    }
                    medicoRepo.atualizar(medico);
                    JOptionPane.showMessageDialog(this,"Dados do médico atualizados!");
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,"Erro: "+ex.getMessage());
                }
            });
        }

        gbc.gridx=1; gbc.gridy=row+1; centerPanel.add(btnSalvar, gbc);
        add(new JScrollPane(centerPanel), BorderLayout.CENTER);
    }
}
