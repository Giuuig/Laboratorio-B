package com.bookrecommender.server.dao;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/** Gestione valutazioni e aggregati. */
public class RatingDAO {

    public void inserisci(int utenteId, int libroId,
                          int stile, int contenuto, int gradevolezza,
                          int originalita, int edizione,
                          String note) throws Exception {
        String sql = "INSERT INTO ValutazioniLibri(utente_id,libro_id,stile,contenuto,gradevolezza,originalita,edizione,note) " +
                "VALUES (?,?,?,?,?,?,?,?) " +
                "ON CONFLICT(utente_id,libro_id) DO UPDATE SET " +
                "stile=EXCLUDED.stile, contenuto=EXCLUDED.contenuto, gradevolezza=EXCLUDED.gradevolezza, " +
                "originalita=EXCLUDED.originalita, edizione=EXCLUDED.edizione, note=EXCLUDED.note";
        try (PreparedStatement ps = DBManager.get().prepareStatement(sql)){
            ps.setInt(1, utenteId);
            ps.setInt(2, libroId);
            ps.setInt(3, stile);
            ps.setInt(4, contenuto);
            ps.setInt(5, gradevolezza);
            ps.setInt(6, originalita);
            ps.setInt(7, edizione);
            ps.setString(8, note);
            ps.executeUpdate();
        }
    }

    /** Aggregati per 5 criteri + voto finale. */
    public Map<String,Object> aggregati(int libroId) throws Exception {
        Map<String,Object> out = new HashMap<>();
        String[] campi = {"stile","contenuto","gradevolezza","originalita","edizione","voto_finale"};
        String base = "SELECT " +
                "COUNT(*) FILTER (WHERE %1$s=1) c1, " +
                "COUNT(*) FILTER (WHERE %1$s=2) c2, " +
                "COUNT(*) FILTER (WHERE %1$s=3) c3, " +
                "COUNT(*) FILTER (WHERE %1$s=4) c4, " +
                "COUNT(*) FILTER (WHERE %1$s=5) c5, " +
                "ROUND(AVG(%1$s),2) media " +
                "FROM ValutazioniLibri WHERE libro_id=?";
        for (String f : campi){
            String sql = String.format(base, f);
            try (PreparedStatement ps = DBManager.get().prepareStatement(sql)){
                ps.setInt(1, libroId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()){
                    Map<String,Object> m = new HashMap<>();
                    m.put("c1", rs.getInt("c1"));
                    m.put("c2", rs.getInt("c2"));
                    m.put("c3", rs.getInt("c3"));
                    m.put("c4", rs.getInt("c4"));
                    m.put("c5", rs.getInt("c5"));
                    Object mediaObj = rs.getObject("media");
                    m.put("media", mediaObj == null ? null : ((Number)mediaObj).doubleValue());
                    out.put(f, m);
                }
            }
        }
        return out;
    }

    public List<Map<String,Object>> reviewsWithUsers(int libroId) throws Exception {
        List<Map<String,Object>> out = new ArrayList<>();
        String sql = "SELECT utente_id, voto_finale, note FROM ValutazioniLibri " +
                     "WHERE libro_id=? " +
                     "ORDER BY utente_id";
        try (PreparedStatement ps = DBManager.get().prepareStatement(sql)){
            ps.setInt(1, libroId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Map<String,Object> m = new HashMap<>();
                m.put("utenteId", rs.getInt("utente_id"));
                Object votoFinale = rs.getObject("voto_finale");
                m.put("votoFinale", votoFinale != null ? ((Number)votoFinale).doubleValue() : 0.0);
                m.put("note", rs.getString("note"));
                out.add(m);
            }
        }
        return out;
    }

}
