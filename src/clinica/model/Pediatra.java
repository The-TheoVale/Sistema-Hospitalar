package clinica.model;
import java.util.List;

public class Pediatra extends Medico{

    public Pediatra(String id, String nome, String senha, double valorConsultaParticular, List<String> planosAtendidos) {
        super(id, nome, senha, "Pediatra", valorConsultaParticular, planosAtendidos);
    }
    
    @Override
    public int maxConsultasDia() { return 2; }
}
