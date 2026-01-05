
package com.bookrecommender.model;

public class UtenteRegistrato extends Utente {
    private String nome;
    private String cognome;
    private String passwordHash;

    public UtenteRegistrato(){}

    public UtenteRegistrato(int id, String email, String nome, String cognome, String passwordHash){
        super(id, email);
        this.nome = nome;
        this.cognome = cognome;
        this.passwordHash = passwordHash;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
