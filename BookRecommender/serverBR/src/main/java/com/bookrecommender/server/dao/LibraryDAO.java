package com.bookrecommender.server.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Librerie personali utente. */
public class LibraryDAO {

    public int creaLibreria(int utenteId, String nome) throws Exception {
        String sql = "INSERT INTO Librerie(utente_id,nome) VALUES (?,?) RETURNING id";
        try (PreparedStatement ps = DBManager.get().prepareStatement(sql)){
            ps.setInt(1, utenteId);
            ps.setString(2, nome);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        }
    }

    public void aggiungiLibri(int libreriaId, List<Integer> libroIds) throws Exception {
        String sql = "INSERT INTO Librerie_Libri(libreria_id,libro_id) VALUES (?,?) ON CONFLICT DO NOTHING";
        try (PreparedStatement ps = DBManager.get().prepareStatement(sql)){
            for (Integer id : libroIds){
                ps.setInt(1, libreriaId);
                ps.setInt(2, id);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public boolean libroNelleMieLibrerie(int utenteId, int libroId) throws Exception {
        String sql = "SELECT 1 FROM Librerie l JOIN Librerie_Libri ll ON l.id=ll.libreria_id WHERE l.utente_id=? AND ll.libro_id=?";
        try (PreparedStatement ps = DBManager.get().prepareStatement(sql)){
            ps.setInt(1, utenteId);
            ps.setInt(2, libroId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    public List<String> lista(int utenteId) throws Exception {
        String sql = "SELECT nome FROM Librerie WHERE utente_id=? ORDER BY nome";
        try (PreparedStatement ps = DBManager.get().prepareStatement(sql)){
            ps.setInt(1, utenteId);
            ResultSet rs = ps.executeQuery();
            List<String> out = new ArrayList<>();
            while (rs.next()) out.add(rs.getString(1));
            return out;
        }
    }

    public java.util.Map<Integer, String> listaConId(int utenteId) throws Exception {
        String sql = "SELECT id, nome FROM Librerie WHERE utente_id=? ORDER BY nome";
        try (PreparedStatement ps = DBManager.get().prepareStatement(sql)){
            ps.setInt(1, utenteId);
            ResultSet rs = ps.executeQuery();
            java.util.Map<Integer, String> map = new java.util.HashMap<>();
            while (rs.next()) {
                map.put(rs.getInt(1), rs.getString(2));
            }
            return map;
        }
    }

    public List<Integer> libriInLibreria(int libreriaId) throws Exception {
        String sql = "SELECT libro_id FROM Librerie_Libri WHERE libreria_id=? ORDER BY libro_id";
        try (PreparedStatement ps = DBManager.get().prepareStatement(sql)){
            ps.setInt(1, libreriaId);
            ResultSet rs = ps.executeQuery();
            List<Integer> ids = new ArrayList<>();
            while (rs.next()) ids.add(rs.getInt(1));
            return ids;
        }
    }
}
