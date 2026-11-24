package clinica.model;

public enum StatusConsulta {
    AGENDADA("Agendada"),
    REALIZADA("Realizada");

    private final String valorBanco;

    StatusConsulta(String valorBanco) { this.valorBanco = valorBanco; }

    public String getValorBanco() { return valorBanco; }

    public static StatusConsulta fromBanco(String valor) {
        for (StatusConsulta s : values()) {
            if (s.valorBanco.equalsIgnoreCase(valor)) return s;
        }
        throw new IllegalArgumentException("Status de consulta desconhecido: " + valor);
    }
}
