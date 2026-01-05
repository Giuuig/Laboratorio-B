package com.bookrecommender.server.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/** Gestione utenti registrati. */
public class UserDAO {

    public int register(String nome, String cognome, String codiceFiscale, String email, String userid, String passwordHash) throws Exception {

        String sql = "INSERT INTO UtentiRegistrati(" +
                     "nome, cognome, codice_fiscale, email, userid, password_hash" +
                     ") VALUES (?,?,?,?,?,?) RETURNING id";

        try (PreparedStatement ps = DBManager.get().prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setString(2, cognome);
            ps.setString(3, codiceFiscale);
            ps.setString(4, email);
            ps.setString(5, userid);
            ps.setString(6, passwordHash);

            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        }
    }

    public Integer login(String userid, String passwordHash) throws Exception {
        String sql = "SELECT id FROM UtentiRegistrati WHERE (userid=? OR email=?) AND password_hash=?";
        try (PreparedStatement ps = DBManager.get().prepareStatement(sql)){
            ps.setString(1, userid);
            ps.setString(2, userid);
            ps.setString(3, passwordHash);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
            return null;
        }
    }

    public String getNomeById(int userId) throws Exception {
        String sql = "SELECT nome FROM UtentiRegistrati WHERE id=?";
        try (PreparedStatement ps = DBManager.get().prepareStatement(sql)){
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("nome");
            return "";
        }
    }
}
