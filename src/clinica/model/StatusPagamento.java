package clinica.model;

public enum StatusPagamento {
    PENDENTE("Pendente"),
    PAGA("Paga");

    private final String valorBanco;

    StatusPagamento(String valorBanco) { this.valorBanco = valorBanco; }

    public String getValorBanco() { return valorBanco; }

    public static StatusPagamento fromBanco(String valor) {
        for (StatusPagamento s : values()) {
            if (s.valorBanco.equalsIgnoreCase(valor)) return s;
        }
        throw new IllegalArgumentException("Situação de conta desconhecida: " + valor);
    }
}
