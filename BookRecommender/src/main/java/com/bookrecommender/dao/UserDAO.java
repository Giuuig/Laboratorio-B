
package com.bookrecommender.dao;

import com.bookrecommender.model.UtenteRegistrato;
import java.sql.*;

public class UserDAO {

    public UtenteRegistrato findByEmail(String email) throws SQLException {
        String q = "SELECT * FROM utenti_registrati WHERE email=?";
        try (PreparedStatement ps = DBManager.getConnection().prepareStatement(q)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                UtenteRegistrato u = new UtenteRegistrato();
                u.setId(rs.getInt("id"));
                u.setEmail(rs.getString("email"));
                u.setNome(rs.getString("nome"));
                u.setCognome(rs.getString("cognome"));
                u.setPasswordHash(rs.getString("password_hash"));
                return u;
            }
            return null;
        }
    }

    public UtenteRegistrato create(String nome, String cognome, String email, String passwordHash) throws SQLException {
        String q = "INSERT INTO utenti_registrati(nome,cognome,email,password_hash) VALUES (?,?,?,?) RETURNING id";
        try (PreparedStatement ps = DBManager.getConnection().prepareStatement(q)) {
            ps.setString(1, nome); ps.setString(2, cognome); ps.setString(3, email); ps.setString(4, passwordHash);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return new UtenteRegistrato(rs.getInt(1), email, nome, cognome, passwordHash);
        }
    }

    public boolean checkPassword(String email, String passwordHash) throws SQLException {
        UtenteRegistrato u = findByEmail(email);
        return u != null && u.getPasswordHash().equals(passwordHash);
    }
}
