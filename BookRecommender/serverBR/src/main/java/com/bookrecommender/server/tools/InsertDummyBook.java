package com.bookrecommender.server.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class InsertDummyBook {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:postgresql://localhost:5432/bookrecommender";
        String user = System.getenv().getOrDefault("BR_DB_USER", "postgres");
        String pwd  = System.getenv().getOrDefault("BR_DB_PASSWORD", "postgres");
        try (Connection c = DriverManager.getConnection(url, user, pwd)) {
            String sql = "INSERT INTO Libri(titolo,autore,anno,genere,descrizione) VALUES (?,?,?,?,?)";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, "__DUMMY__");
                ps.setString(2, "system");
                ps.setInt(3, 2025);
                ps.setString(4, "auto");
                ps.setString(5, "dummy entry to avoid seed parsing errors");
                ps.executeUpdate();
                System.out.println("Inserted dummy book.");
            }
        }
    }
}
