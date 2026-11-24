package clinica.model;

/** Mapeia a tabela {@code plano} do banco. */
public class Plano {
    private int idPlano;
    private String nomeDoPlano;
    private String telefoneDeContato;

    public Plano(int idPlano, String nomeDoPlano, String telefoneDeContato) {
        this.idPlano = idPlano;
        this.nomeDoPlano = nomeDoPlano;
        this.telefoneDeContato = telefoneDeContato;
    }

    public int getIdPlano()                      { return idPlano; }
    public String getNomeDoPlano()               { return nomeDoPlano; }
    public String getTelefoneDeContato()         { return telefoneDeContato; }
    public void setNomeDoPlano(String n)         { this.nomeDoPlano = n; }
    public void setTelefoneDeContato(String t)   { this.telefoneDeContato = t; }

    @Override
    public String toString() { return nomeDoPlano; }
}
