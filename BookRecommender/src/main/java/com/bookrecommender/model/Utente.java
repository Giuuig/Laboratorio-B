
package com.bookrecommender.model;

public class Utente {
    protected int id;
    protected String email;

    public Utente() {}

    public Utente(int id, String email) {
        this.id = id;
        this.email = email;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
