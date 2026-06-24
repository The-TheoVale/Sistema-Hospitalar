package clinica.model;

public abstract class Usuario {
    protected String id;
    protected String nome;
    protected String senha;
    
    
    public Usuario(String id, String nome, String senha) {
        this.id = id;
        this.nome = nome;
        this.senha = senha;
    }


    public String getId() {
        return id;
    }


    public String getNome() {
        return nome;
    }


    public String getSenha() {
        return senha;
    }


    public void setNome(String nome) { this.nome = nome; }

    public void setSenha(String senha) { this.senha = senha; }

}