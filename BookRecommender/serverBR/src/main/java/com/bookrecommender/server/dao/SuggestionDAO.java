package com.bookrecommender.server.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Consigli libri: max 3 per (utente, libro) a livello applicativo. */
public class SuggestionDAO {

    public int countForUserBook(int utenteId, int libroId) throws Exception {
        String sql = "SELECT COUNT(*) FROM ConsigliLibri WHERE utente_id=? AND libro_id=?";
        try (PreparedStatement ps = DBManager.get().prepareStatement(sql)){
            ps.setInt(1, utenteId);
            ps.setInt(2, libroId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        }
    }

    public void inserisci(int utenteId, int libroId, List<Integer> suggeriti) throws Exception {
        // Prima elimina i suggerimenti esistenti per questo utente e libro
        String deleteSql = "DELETE FROM ConsigliLibri WHERE utente_id=? AND libro_id=?";
        try (PreparedStatement ps = DBManager.get().prepareStatement(deleteSql)){
            ps.setInt(1, utenteId);
            ps.setInt(2, libroId);
            ps.executeUpdate();
        }
        
        // Ora inserisci i nuovi suggerimenti (max 3)
        if (suggeriti.size() > 3){
            throw new IllegalArgumentException("Massimo 3 suggerimenti per libro/utente");
        }
        
        String sql = "INSERT INTO ConsigliLibri(utente_id,libro_id,suggerito_libro_id) VALUES (?,?,?)";
        try (PreparedStatement ps = DBManager.get().prepareStatement(sql)){
            for (Integer sid : suggeriti){
                ps.setInt(1, utenteId);
                ps.setInt(2, libroId);
                ps.setInt(3, sid);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /** Recupera i libri suggeriti da un utente per un libro specifico. */
    public List<String> getTitoliSuggeritiPerUtenteLibro(int utenteId, int libroId) throws Exception {
        List<String> titoli = new ArrayList<>();
        String sql = "SELECT l.titolo FROM ConsigliLibri c " +
                     "JOIN Libri l ON c.suggerito_libro_id = l.id " +
                     "WHERE c.utente_id=? AND c.libro_id=? " +
                     "ORDER BY l.titolo";
        try (PreparedStatement ps = DBManager.get().prepareStatement(sql)){
            ps.setInt(1, utenteId);
            ps.setInt(2, libroId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                titoli.add(rs.getString("titolo"));
            }
        }
        return titoli;
    }

    /** Per visualizzaLibro: conteggio di quante volte ogni libro è stato consigliato. */
    public List<Map<String,Object>> conteggioPerLibro(int libroId) throws Exception {
        String sql = "SELECT suggerito_libro_id, COUNT(*) n FROM ConsigliLibri WHERE libro_id=? GROUP BY suggerito_libro_id ORDER BY n DESC";
        try (PreparedStatement ps = DBManager.get().prepareStatement(sql)){
            ps.setInt(1, libroId);
            ResultSet rs = ps.executeQuery();
            List<Map<String,Object>> out = new ArrayList<>();
            while (rs.next()){
                Map<String,Object> m = new HashMap<>();
                m.put("libroId", rs.getInt(1));
                m.put("count", rs.getInt(2));
                out.add(m);
            }
            return out;
        }
    }
}
