package clinica.model;
import java.util.ArrayList;
import java.util.List;

public abstract class Medico extends Usuario{
    protected String especialidade;
    protected List<String> planosAtendidos;
    protected double valorConsultaParticular;
    protected List<Avaliacao> avaliacoes;
    
    public Medico(String id, String nome, String senha, String especialidade,double valorConsultaParticular, List<String> planosAtendidos) {
        super(id, nome, senha);
        this.planosAtendidos = planosAtendidos;
        avaliacoes = new ArrayList<>();
        this.especialidade = especialidade;
        this.valorConsultaParticular = valorConsultaParticular;
}

    public int maxConsultasDia(){
        return 3;
    }
    public double getMediaEstrelas(){
        return avaliacoes.stream().mapToInt(Avaliacao::getEstrelas).average().orElse(0);
    }
    public void adicionarAvaliacao(Avaliacao avaliacao){
        avaliacoes.add(avaliacao);
    }

    public boolean aceitaPlano (String plano){
        return planosAtendidos.contains(plano);
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public List<String> getPlanosAtendidos() {
        return planosAtendidos;
    }

    public double getValorConsultaParticular() {
        return valorConsultaParticular;
    }

    public List<Avaliacao> getAvaliacoes() {
        return avaliacoes;
    }
    
    public double getValorConsulta() {
        return valorConsultaParticular;
    }
}
