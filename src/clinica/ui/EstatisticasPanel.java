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
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(240, 245, 250)); // fundo suave

        // Título
        JLabel lblTitulo = new JLabel("📊 Estatísticas do Sistema", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(30, 60, 90));
        add(lblTitulo, BorderLayout.NORTH);

        // Painel central com cards
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(0, 2, 20, 20)); // grid com espaçamento
        centerPanel.setOpaque(false);

        // Estatísticas em cards
        Medico melhor = estatisticasService.medicoMaisBemAvaliado();
        String melhorMedico = (melhor != null) ? melhor.getNome() + " (" + melhor.getMediaEstrelas() + "⭐)" : "Nenhum médico avaliado";
        centerPanel.add(criarCard("Médico mais bem avaliado", melhorMedico, new Color(173, 216, 230)));

        centerPanel.add(criarCard("Especialidade mais procurada", estatisticasService.especialidadeMaisProcurada(), new Color(255, 228, 196)));

        centerPanel.add(criarCard("Total de consultas realizadas", String.valueOf(estatisticasService.totalConsultasRealizadas()), new Color(144, 238, 144)));

        centerPanel.add(criarCard("Média geral de avaliações", String.valueOf(estatisticasService.mediaAvaliacoes()), new Color(255, 182, 193)));

        // Gráfico em card
        Map<String, Integer> consultasPorMedico = estatisticasService.consultasPorMedico();
        JPanel graficoCard = criarCard("Distribuição de consultas por médico (Top 5)", "", new Color(240, 230, 140));
        graficoCard.setLayout(new BorderLayout());

        // Dá mais espaço para o gráfico de pizza
        GraficoPizzaPanel graficoPizza = new GraficoPizzaPanel(consultasPorMedico, medicoRepo);
        graficoPizza.setPreferredSize(new Dimension(500, 400)); // espaço maior
        graficoCard.add(graficoPizza, BorderLayout.CENTER);

        centerPanel.add(graficoCard);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel criarCard(String titulo, String valor, Color corFundo) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(corFundo);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblTitulo);

        if (!valor.isEmpty()) {
            JLabel lblValor = new JLabel(valor);
            lblValor.setFont(new Font("SansSerif", Font.PLAIN, 16));
            lblValor.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(Box.createRigidArea(new Dimension(0, 10)));
            card.add(lblValor);
        }

        return card;
    }

    // Painel customizado para gráfico de pizza (Top 5 médicos)
    static class GraficoPizzaPanel extends JPanel {
        private final Map<String, Integer> dados;
        private final MedicoRepository medicoRepo;
        private final Color[] cores = {
                new Color(100, 149, 237),
                new Color(255, 99, 71),
                new Color(60, 179, 113),
                new Color(238, 130, 238),
                new Color(255, 215, 0)
        };

        public GraficoPizzaPanel(Map<String, Integer> dados, MedicoRepository medicoRepo) {
            this.dados = dados;
            this.medicoRepo = medicoRepo;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (dados.isEmpty()) return;

            // Ordena os médicos por número de consultas (descendente) e pega apenas os 5 primeiros
            var top5 = dados.entrySet().stream()
                    .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                    .limit(5)
                    .toList();

            int total = top5.stream().mapToInt(Map.Entry::getValue).sum();
            int startAngle = 0;
            int i = 0;

            Graphics2D g2d = (Graphics2D) g;
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 14));

            // Desenha pizza maior
            int diameter = Math.min(getWidth() - 200, getHeight() - 50);
            int x = 30;
            int y = 30;

            for (Map.Entry<String, Integer> entry : top5) {
                int valor = entry.getValue();
                int angle = (int) Math.round(((double) valor / total) * 360);

                g2d.setColor(cores[i % cores.length]);
                g2d.fillArc(x, y, diameter, diameter, startAngle, angle);

                // Legenda ao lado
                g2d.setColor(Color.BLACK);
                String nomeMedico = medicoRepo.buscarPorId(entry.getKey()).getNome();
                g2d.drawString(nomeMedico + " - " + valor + " consultas", diameter + 60, 20 + i * 25);

                startAngle += angle;
                i++;
            }
        }
    }
}