package clinica.service;
import clinica.repositorio.*;
import clinica.model.*;
import java.time.LocalDate;
public class AgendamentoService {
    private ConsultaRepository consultaRepositorio;
    private ListaEsperaRepository listaEsperaRepositorio;

    

    public AgendamentoService(ConsultaRepository consultaRepositorio, ListaEsperaRepository listaEsperaRepositorio) {
        this.consultaRepositorio = consultaRepositorio;
        this.listaEsperaRepositorio = listaEsperaRepositorio;
    }

    public int agendar(Medico medico, Paciente paciente, LocalDate data){
        if (consultaRepositorio.disponibilidadeDoMedico(medico.getId(), data)) {
            consultaRepositorio.cadastrarConsulta(new Consulta(medico, paciente, data));
            return 0;
        }

        
        return listaEsperaRepositorio.adicionarNaFila(medico, paciente, data);
    }


    public boolean cancelar(String identificadorConsulta) {
        Consulta consulta = consultaRepositorio.buscarPorIdentificador(identificadorConsulta);
        if (consulta == null) return false;

        consulta.setStatus(StatusConsulta.CANCELADA);
        consultaRepositorio.salvar();
        

        Paciente promovido = listaEsperaRepositorio.promoverProximo(consulta.getMedico().getId(), consulta.getData());

    if (promovido != null){
        Consulta novaConsulta = new Consulta (consulta.getMedico(), promovido, consulta.getData());
        consultaRepositorio.cadastrarConsulta(novaConsulta);
        consultaRepositorio.salvar();
        NotificacaoRepository notifRepo = new NotificacaoRepository();
        notifRepo.cadastrarNotificacao(new Notificacao(promovido.getId(), "⚠️ SPAM: Você foi promovido da lista de espera e agora possui uma consulta marcada em " + consulta.getData() + " com Dr(a). " + consulta.getMedico().getNome()));
    }

        return true;
    }
}