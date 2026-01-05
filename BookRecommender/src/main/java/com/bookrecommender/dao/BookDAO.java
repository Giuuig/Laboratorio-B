
package com.bookrecommender.dao;

import com.bookrecommender.model.Libro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    public List<Libro> search(String titolo, String autore, Integer anno){
        List<Libro> res = new ArrayList<>();
        StringBuilder sb = new StringBuilder("SELECT * FROM libri WHERE 1=1");
        if (titolo != null && !titolo.isBlank()) sb.append(" AND LOWER(titolo) LIKE LOWER(?)");
        if (autore != null && !autore.isBlank()) sb.append(" AND LOWER(autore) LIKE LOWER(?)");
        if (anno != null) sb.append(" AND anno = ?");
        try (PreparedStatement ps = DBManager.getConnection().prepareStatement(sb.toString())) {
            int i=1;
            if (titolo != null && !titolo.isBlank()) ps.setString(i++, "%" + titolo + "%");
            if (autore != null && !autore.isBlank()) ps.setString(i++, "%" + autore + "%");
            if (anno != null) ps.setInt(i++, anno);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                res.add(new Libro(rs.getInt("id"), rs.getString("titolo"), rs.getString("autore"),
                        (Integer)rs.getObject("anno"), rs.getString("genere"), rs.getString("descrizione")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public Libro create(String titolo, String autore, Integer anno, String genere, String descrizione) throws SQLException {
        String q = "INSERT INTO libri(titolo,autore,anno,genere,descrizione) VALUES (?,?,?,?,?) RETURNING id";
        try (PreparedStatement ps = DBManager.getConnection().prepareStatement(q)){
            ps.setString(1, titolo); ps.setString(2, autore); 
            if (anno == null) ps.setNull(3, Types.INTEGER); else ps.setInt(3, anno);
            ps.setString(4, genere); ps.setString(5, descrizione);
            ResultSet rs = ps.executeQuery(); rs.next();
            return new Libro(rs.getInt(1), titolo, autore, anno, genere, descrizione);
        }
    }
}
