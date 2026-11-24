package clinica.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Utilitários de validação/conversão dos campos de formulário. */
final class FormUtil {
    static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    static final String[] SEXOS = {"M", "F", "O"};

    private FormUtil() { }

    static LocalDate parseData(String texto, String campo) {
        try {
            return LocalDate.parse(texto.trim(), DATA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(campo + " inválida. Use o formato dd/mm/aaaa.");
        }
    }

    static LocalDate parseDataOpcional(String texto, String campo) {
        return (texto == null || texto.isBlank()) ? null : parseData(texto, campo);
    }

    static String formatar(LocalDate data) {
        return data == null ? "" : data.format(DATA);
    }

    static String somenteDigitos(String texto) {
        return texto == null ? "" : texto.replaceAll("\\D", "");
    }

    static String vazioParaNull(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }

    static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new IllegalArgumentException(mensagem);
    }

    static void validarEmail(String email) {
        exigir(email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"), "E-mail inválido.");
    }
}
