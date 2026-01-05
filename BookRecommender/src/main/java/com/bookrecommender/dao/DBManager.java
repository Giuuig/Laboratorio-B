package com.bookrecommender.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Properties;

/** DB bootstrap + seeding alla prima esecuzione. */
public class DBManager {
    private static Connection connection;
    private static Properties props;

static {
    try {
        System.out.println(">>> [DEBUG] Caricamento DBManager...");
        props = new Properties();
        try (InputStream is = DBManager.class.getClassLoader().getResourceAsStream("app.properties")) {
            if (is != null) props.load(is);
        }
        ensureDatabase();
        connection = DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.user"),
                props.getProperty("db.password"));

        // crea schema
        System.out.println(">>> [DEBUG] Invocazione runSchema...");
        runSchema();

        // verifica seed
        int count = countLibri();
        System.out.println(">>> [DEBUG] Libri attuali nel DB: " + count);
        if (count == 0) {
            System.out.println(">>> [DEBUG] Nessun libro trovato, avvio seedBooks...");
            seedBooks();
        }
    } catch (Exception e) {
        throw new RuntimeException("DB init error: " + e.getMessage(), e);
    }
}



    private static void ensureDatabase() throws SQLException {
        // Connect to postgres default db to create our DB if missing
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");
        String dbName = url.substring(url.lastIndexOf('/') + 1);
        String adminUrl = url.substring(0, url.lastIndexOf('/')) + "/postgres";
        try (Connection c = DriverManager.getConnection(adminUrl, user, password)) {
            try (PreparedStatement ps = c.prepareStatement("SELECT 1 FROM pg_database WHERE datname = ?")) {
                ps.setString(1, dbName);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    try (Statement st = c.createStatement()) {
                        st.executeUpdate("CREATE DATABASE " + dbName);
                    }
                }
            }
        }
    }

    private static void runSchema() throws IOException, SQLException {
        System.out.println(">>> [DEBUG] Avvio runSchema...");

        InputStream is = DBManager.class.getClassLoader().getResourceAsStream("schema.sql");
        if (is == null) {
            System.out.println(">>> [DEBUG] ERRORE: schema.sql non trovato nei resources!");
            return;
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }

        String[] statements = sb.toString().split(";");
        try (Statement st = connection.createStatement()) {
            for (String stmt : statements) {
                String sql = stmt.trim();
                if (!sql.isEmpty()) {
                    try {
                        st.execute(sql);
                        System.out.println(">>> [DEBUG] Eseguito: " + sql.substring(0, Math.min(60, sql.length())) + "...");
                    } catch (SQLException e) {
                        System.out.println(">>> [DEBUG] ERRORE su query: " + e.getMessage());
                    }
                }
            }
        }
        System.out.println(">>> [DEBUG] Schema completato.");
    }




    // NUOVO: conta libri per capire se fare il seed alla prima esecuzione
    private static int countLibri() throws SQLException {
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM libri")) {
            rs.next();
            return rs.getInt(1);
        }
    }

private static void seedBooks() {
    System.out.println("[SEED] Avvio seed libri da CSV...");
    try (InputStream is = DBManager.class.getClassLoader().getResourceAsStream("BooksDatasetClean.csv")) {
        if (is == null) {
            System.out.println("[SEED] ERRORE: CSV BooksDatasetClean.csv non trovato nei resources!");
            return;
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            int inserted = 0, skipped = 0;
            br.readLine(); // salta intestazione
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";", -1);
                if (parts.length < 4) {
                    skipped++;
                    continue;
                }
                String titolo = parts[0].trim();
                String autore = parts[1].trim();
                Integer anno = null;
                try { anno = Integer.parseInt(parts[2].trim()); } catch (Exception ignore) {}
                String genere = parts[3].trim();

                try (PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO libri (titolo, autore, anno, genere) VALUES (?, ?, ?, ?)")) {
                    ps.setString(1, titolo);
                    ps.setString(2, autore);
                    if (anno != null) ps.setInt(3, anno); else ps.setNull(3, Types.INTEGER);
                    ps.setString(4, genere.isEmpty() ? null : genere);
                    ps.executeUpdate();
                    inserted++;
                } catch (SQLException e) {
                    skipped++;
                    System.out.println("[SEED] Riga saltata: " + e.getMessage());
                }
            }
            System.out.println("[SEED] Completato: inseriti=" + inserted + ", saltati=" + skipped);
        }
    } catch (Exception e) {
        System.out.println("[SEED] ERRORE: " + e.getMessage());
    }
}




    public static Connection getConnection() { return connection; }
}
