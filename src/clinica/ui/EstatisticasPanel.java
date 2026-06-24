package clinica.ui;

import clinica.model.Medico;
import clinica.repositorio.MedicoRepository;
import clinica.repositorio.ConsultaRepository;
import clinica.service.EstatisticasService;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class EstatisticasPanel extends JPanel {

    private final EstatisticasService estatisticasService;
    private final MedicoRepository medicoRepo;

    public EstatisticasPanel(MedicoRepository medicoRepo, ConsultaRepository consultaRepo) {
        this.medicoRepo = medicoRepo;
        this.estatisticasService = new EstatisticasService(medicoRepo, consultaRepo);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = new JLabel("Estatísticas do Sistema", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(lblTitulo);
        add(Box.createRigidArea(new Dimension(0, 20)));

        // Médico mais bem avaliado
        Medico melhor = estatisticasService.medicoMaisBemAvaliado();
        String melhorMedico = (melhor != null) ? melhor.getNome() + " (" + melhor.getMediaEstrelas() + " estrelas)" : "Nenhum médico avaliado";
        add(criarLabel("Médico mais bem avaliado: " + melhorMedico));

        // Especialidade mais procurada
        add(criarLabel("Especialidade mais procurada: " + estatisticasService.especialidadeMaisProcurada()));

        // Total de consultas realizadas
        add(criarLabel("Total de consultas realizadas: " + estatisticasService.totalConsultasRealizadas()));

        // Média geral de avaliações
        add(criarLabel("Média geral de avaliações: " + estatisticasService.mediaAvaliacoes()));

        // Consultas por médico
        add(criarLabel("Consultas por médico:"));
        Map<String, Integer> consultasPorMedico = estatisticasService.consultasPorMedico();
        for (Map.Entry<String, Integer> entry : consultasPorMedico.entrySet()) {
            add(criarLabel(" - Médico " + medicoRepo.buscarPorId(entry.getKey()).getNome() + ": " + entry.getValue() + " consultas"));
        }
    }

    private JLabel criarLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 18));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }
}
