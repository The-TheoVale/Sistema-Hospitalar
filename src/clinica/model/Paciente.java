package clinica.model;

import java.util.ArrayList;
import java.util.List;
public class Paciente extends Usuario{
    private String planoSaude;
    private List<Consulta> historico;
    private int idade;

    public Paciente(String id, String nome, String senha, int idade, String planoSaude) {
        super(id, nome, senha);
        this.idade = idade;
        this.planoSaude = planoSaude;
        historico = new ArrayList<>();
    }

    public void adicionarAoHistorico(Consulta consulta){
        historico.add(consulta);
    }

    public String getPlanoSaude() {
        return planoSaude;
    }

    public List<Consulta> getHistorico() {
        return historico;
    }

    public int getIdade() {
        return idade;
    }
    
    public Boolean temPlano(){
        
        return !"não tenho".equals(this.planoSaude);
    }

    
}
