package clinica.model;

public class Avaliacao {
    private Consulta consulta;
    private String texto;
    private int estrelas;
    private StatusAvaliacao status;

    
    public Avaliacao(Consulta consulta) {
        this.consulta = consulta;
        status = StatusAvaliacao.PENDENTE;
    }
    public String getTexto() {
        return texto;
    }
    public int getEstrelas() {
        return estrelas;
    }
    public Consulta getConsulta() {
        return consulta;
    }

    public void responder(String texto, int estrelas) {
        this.texto = texto;
        this.estrelas = estrelas;
        this.status = StatusAvaliacao.CONCLUIDA;
    }
    
    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }
    public StatusAvaliacao getStatus() {
        return status;
    }
    public void setStatus(StatusAvaliacao status) {
        this.status = status;
    }

    
}
