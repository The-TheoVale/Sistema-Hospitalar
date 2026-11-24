package clinica.model;

import java.time.LocalDate;

public abstract class Usuario {
    protected int id;
    protected String nome;
    protected String sobrenome;
    protected String email;
    protected String senha;
    protected LocalDate dataNascimento;
    protected char sexo;
    protected String telefone;

    public Usuario(int id, String nome, String sobrenome, String email, String senha,
                   LocalDate dataNascimento, char sexo, String telefone) {
        this.id = id;
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.email = email;
        this.senha = senha;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.telefone = telefone;
    }

    public int getId()                     { return id; }
    public String getNome()                { return nome; }
    public String getSobrenome()           { return sobrenome; }
    public String getEmail()               { return email; }
    public String getSenha()               { return senha; }
    public LocalDate getDataNascimento()   { return dataNascimento; }
    public char getSexo()                  { return sexo; }
    public String getTelefone()            { return telefone; }

    public void setNome(String nome)             { this.nome = nome; }
    public void setSobrenome(String sobrenome)   { this.sobrenome = sobrenome; }
    public void setEmail(String email)           { this.email = email; }
    public void setSenha(String senha)           { this.senha = senha; }
    public void setTelefone(String telefone)     { this.telefone = telefone; }
    public void setDataNascimento(LocalDate d)   { this.dataNascimento = d; }
    public void setSexo(char sexo)               { this.sexo = sexo; }

    public String getNomeCompleto() {
        return (sobrenome == null || sobrenome.isBlank()) ? nome : nome + " " + sobrenome;
    }
}