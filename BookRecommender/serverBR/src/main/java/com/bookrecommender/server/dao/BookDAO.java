package com.bookrecommender.server.dao;

import com.bookrecommender.server.model.Libro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Query di ricerca libri. */
public class BookDAO {

    private Libro map(ResultSet rs) throws Exception {
        Libro l = new Libro();
        l.id = rs.getInt("id");
        l.titolo = rs.getString("titolo");
        l.autore = rs.getString("autore");
        l.anno = (Integer) rs.getObject("anno");
        l.genere = rs.getString("genere");
        l.descrizione = rs.getString("descrizione");
        return l;
    }

    private List<Libro> mapList(ResultSet rs) throws Exception {
        List<Libro> out = new ArrayList<>();
        while (rs.next()) out.add(map(rs));
        return out;
    }

    public List<Libro> searchByTitle(String q){
        String sql = "SELECT * FROM Libri WHERE LOWER(titolo) LIKE LOWER(?) ORDER BY titolo";
        try (PreparedStatement ps = DBManager.get().prepareStatement(sql)){
            ps.setString(1, "%" + q + "%");
            return mapList(ps.executeQuery());
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public List<Libro> searchByAuthor(String q){
        String sql = "SELECT * FROM Libri WHERE LOWER(autore) LIKE LOWER(?) ORDER BY titolo";
        try (PreparedStatement ps = DBManager.get().prepareStatement(sql)){
            ps.setString(1, "%" + q + "%");
            return mapList(ps.executeQuery());
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public List<Libro> searchByAuthorYear(String autore, int anno){
        String sql = "SELECT * FROM Libri WHERE LOWER(autore) LIKE LOWER(?) AND anno=? ORDER BY titolo";
        try (PreparedStatement ps = DBManager.get().prepareStatement(sql)){
            ps.setString(1, "%" + autore + "%");
            ps.setInt(2, anno);
            return mapList(ps.executeQuery());
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public Libro findById(int id){
        String sql = "SELECT * FROM Libri WHERE id=?";
        try (PreparedStatement ps = DBManager.get().prepareStatement(sql)){
            ps.setInt(1, id);
            var rs = ps.executeQuery();
            if (rs.next()) return map(rs);
            return null;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
