package model;

public class Cliente {
    private Long ID;
    private String nome;
    private String telefone;

    public Cliente() {
    }

    public Cliente(String nome, String telefone,Long ID) {
        this.ID=ID;
        this.nome = nome;
        this.telefone = telefone;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    

}
