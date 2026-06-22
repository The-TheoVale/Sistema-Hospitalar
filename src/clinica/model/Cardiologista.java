package clinica.model;
import java.util.List;
public class Cardiologista extends Medico{
    private static final double ADICIONAL = 150;

    public Cardiologista(String id, String nome, String senha, double valorConsultaParticular, List<String> planosAtendidos) {
        super(id, nome, senha, "Cardiologista", valorConsultaParticular, planosAtendidos);
    }
    
    @Override
    public double getValorConsulta() {
        return super.getValorConsulta() + ADICIONAL;
    }
}
