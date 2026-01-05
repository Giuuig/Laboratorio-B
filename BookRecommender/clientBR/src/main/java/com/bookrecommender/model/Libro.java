package com.bookrecommender.model;

/** Modello libro lato client, allineato al JSON restituito dal server. */
public class Libro {
    private int id;
    private String titolo;
    private String autore;
    private Integer anno;
    private String genere;
    private String descrizione;

    public Libro() {}

    public Libro(int id, String titolo, String autore, Integer anno, String genere, String descrizione) {
        this.id = id;
        this.titolo = titolo;
        this.autore = autore;
        this.anno = anno;
        this.genere = genere;
        this.descrizione = descrizione;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }
    public String getAutore() { return autore; }
    public void setAutore(String autore) { this.autore = autore; }
    public Integer getAnno() { return anno; }
    public void setAnno(Integer anno) { this.anno = anno; }
    public String getGenere() { return genere; }
    public void setGenere(String genere) { this.genere = genere; }
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    @Override
    public String toString(){ return titolo + " - " + autore; }
}
