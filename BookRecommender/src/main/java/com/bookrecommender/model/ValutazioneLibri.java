
package com.bookrecommender.model;

public class ValutazioneLibri {
    private int id;
    private int userId;
    private int libroId;
    private int voto;
    private String commento;

    public ValutazioneLibri(){}

    public ValutazioneLibri(int id, int userId, int libroId, int voto, String commento){
        this.id = id; this.userId = userId; this.libroId = libroId; this.voto = voto; this.commento = commento;
    }

    public int getId(){ return id; }
    public void setId(int id){ this.id = id; }
    public int getUserId(){ return userId; }
    public void setUserId(int userId){ this.userId = userId; }
    public int getLibroId(){ return libroId; }
    public void setLibroId(int libroId){ this.libroId = libroId; }
    public int getVoto(){ return voto; }
    public void setVoto(int voto){ this.voto = voto; }
    public String getCommento(){ return commento; }
    public void setCommento(String commento){ this.commento = commento; }
}
