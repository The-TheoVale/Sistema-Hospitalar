package clinica.model;
import java.util.List;
public class ClinicoGeral extends Medico{

    public ClinicoGeral(String id, String nome, String senha, double valorConsultaParticular, List<String> planosAtendidos) {
        super(id, nome, senha, "Clínico Geral", valorConsultaParticular, planosAtendidos);
    }
    
}
