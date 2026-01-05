
package com.bookrecommender.model;

public class Libreria {
    private int id;
    private int userId;
    private String nome;

    public Libreria(){}

    public Libreria(int id, int userId, String nome){
        this.id = id; this.userId = userId; this.nome = nome;
    }

    public int getId(){ return id; }
    public void setId(int id){ this.id = id; }
    public int getUserId(){ return userId; }
    public void setUserId(int userId){ this.userId = userId; }
    public String getNome(){ return nome; }
    public void setNome(String nome){ this.nome = nome; }
}
