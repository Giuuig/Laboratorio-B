
package com.bookrecommender.model;

public class ConsiglioLibri {
    private int id;
    private int userId;
    private int libroId;
    private String motivo;

    public ConsiglioLibri(){}

    public ConsiglioLibri(int id, int userId, int libroId, String motivo){
        this.id = id; this.userId = userId; this.libroId = libroId; this.motivo = motivo;
    }

    public int getId(){ return id; }
    public void setId(int id){ this.id = id; }
    public int getUserId(){ return userId; }
    public void setUserId(int userId){ this.userId = userId; }
    public int getLibroId(){ return libroId; }
    public void setLibroId(int libroId){ this.libroId = libroId; }
    public String getMotivo(){ return motivo; }
    public void setMotivo(String motivo){ this.motivo = motivo; }
}
