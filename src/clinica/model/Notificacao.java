package clinica.model;

public class Notificacao {
    private String idPaciente;
    private String mensagem;

    public Notificacao(String idPaciente, String mensagem) {
        this.idPaciente = idPaciente;
        this.mensagem = mensagem;
    }

    public String getIdPaciente() {
        return idPaciente;
    }

    public String getMensagem() {
        return mensagem;
    }
}
