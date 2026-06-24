package clinica.exception;

public class ConsultaInexistenteException extends Exception {
    public ConsultaInexistenteException(String mensagem) {
        super(mensagem);
    }
}