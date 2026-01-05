package com.bookrecommender.server.dao;

import com.bookrecommender.server.model.Libro;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Gestione connessione DB + creazione schema + import da CSV BooksDatasetClean.csv
 * (solo alla prima esecuzione, se la tabella Libri è vuota).
 */
public class DBManager {

    private static Connection conn;

    public static void init() {
        if (conn != null) return;
        try {
            Properties defaults = new Properties();
            try (InputStream is = DBManager.class.getClassLoader().getResourceAsStream("application.properties")) {
                if (is == null) throw new IllegalStateException("application.properties non trovato nel classpath");
                defaults.load(is);
            }

            String url = System.getenv().getOrDefault("BR_DB_URL", defaults.getProperty("db.url"));
            String user = System.getenv().getOrDefault("BR_DB_USER", defaults.getProperty("db.user"));
            String pwd  = System.getenv().getOrDefault("BR_DB_PASSWORD", defaults.getProperty("db.password"));

            try {
                conn = DriverManager.getConnection(url, user, pwd);
            } catch (SQLException ex) {
                // se fallisce e siamo su Postgres, proviamo a creare il DB quindi riconnettere
                if (url != null && url.startsWith("jdbc:postgresql:")) {
                    createPostgresDatabaseIfMissing(url, user, pwd);
                    conn = DriverManager.getConnection(url, user, pwd);
                } else {
                    throw ex;
                }
            }
            conn.setAutoCommit(true);

            applySchema();
            seedIfEmpty();
        } catch (Exception e){
            throw new RuntimeException("Errore inizializzazione DB: " + e.getMessage(), e);
        }
    }

    // Prova a connettersi alla DB 'postgres' e creare il database target se non esiste
    private static void createPostgresDatabaseIfMissing(String url, String user, String pwd) throws SQLException {
        // url es. jdbc:postgresql://host:5432/dbname oppure con parametri
        String withoutPrefix = url.substring("jdbc:postgresql://".length());
        String hostPortAndDb = withoutPrefix;
        int slash = hostPortAndDb.indexOf('/');
        if (slash < 0) return; // forma non riconosciuta
        String hostPort = hostPortAndDb.substring(0, slash);
        String dbAndParams = hostPortAndDb.substring(slash + 1);
        String dbName = dbAndParams.split("[?]")[0];

        String adminUrl = "jdbc:postgresql://" + hostPort + "/postgres";

        try (Connection adminConn = DriverManager.getConnection(adminUrl, user, pwd);
             PreparedStatement ps = adminConn.prepareStatement("SELECT 1 FROM pg_database WHERE datname=?")) {
            ps.setString(1, dbName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // esiste
                    return;
                }
            }
            try (Statement st = adminConn.createStatement()) {
                st.executeUpdate("CREATE DATABASE \"" + dbName + "\"");
                System.out.println("[DB] Database '" + dbName + "' creato automaticamente.");
            }
        }
    }

    private static void applySchema() throws Exception {
        try (InputStream is = DBManager.class.getClassLoader().getResourceAsStream("db/schema.sql")) {
            if (is == null) throw new IllegalStateException("db/schema.sql non trovato");
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            }
            String[] statements = sb.toString().split(";");
            try (Statement st = conn.createStatement()) {
                for (String raw : statements) {
                    String sql = raw.trim();
                    if (sql.isEmpty()) continue;
                    st.execute(sql);
                }
            }
        }
    }

    private static void seedIfEmpty() throws Exception {
        // Controlla se ci sono già libri
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Libri")) {
            rs.next();
            int count = rs.getInt(1);
            if (count > 0) return; // già popolato
        }

        // Legge il CSV BooksDatasetClean.csv (delimitatore ';')
        try (InputStream is = DBManager.class.getClassLoader().getResourceAsStream("BooksDatasetClean.csv")) {
            if (is == null) {
                System.out.println("[SEED] BooksDatasetClean.csv non trovato, niente import iniziale.");
                return;
            }
            List<Libro> libri = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String headerLine = br.readLine(); // leggi intestazione
                String[] headerParts = parseCSVLineQuoted(headerLine);
                
                // Identifica colonne per nome
                int titleIdx = findColumnIndex(headerParts, "Title", "Titolo");
                int authorIdx = findColumnIndex(headerParts, "Author", "Autore");
                int yearIdx = findColumnIndex(headerParts, "Year", "Anno", "Publish Date (Year)");
                int categoryIdx = findColumnIndex(headerParts, "Category", "Genere", "Genre");
                int descIdx = findColumnIndex(headerParts, "Description", "Descrizione");
                
                StringBuilder buffer = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    buffer.append(line).append('\n');
                    
                    // Conta i ; per verificare se riga è completa
                    int semiCount = countSemicolons(buffer.toString());
                    if (semiCount >= headerParts.length - 1) {
                        try {
                            String[] parts = parseCSVLineQuoted(buffer.toString());
                            if (parts.length > Math.max(Math.max(titleIdx, authorIdx), Math.max(yearIdx, categoryIdx))) {
                                String titolo = (titleIdx >= 0 && titleIdx < parts.length) ? parts[titleIdx].trim() : "";
                                String autore = (authorIdx >= 0 && authorIdx < parts.length) ? parts[authorIdx].trim() : "";
                                String yearStr = (yearIdx >= 0 && yearIdx < parts.length) ? parts[yearIdx].trim() : "";
                                String genere = (categoryIdx >= 0 && categoryIdx < parts.length) ? parts[categoryIdx].trim() : "";
                                String descrizione = (descIdx >= 0 && descIdx < parts.length) ? parts[descIdx].trim() : "";
                                
                                if (!titolo.isEmpty()) {
                                    Integer anno = null;
                                    if (!yearStr.isEmpty() && !yearStr.equals("N/A")) {
                                        try {
                                            anno = Integer.valueOf(yearStr);
                                        } catch (NumberFormatException ignored) {}
                                    }
                                    
                                    Libro l = new Libro();
                                    l.titolo = titolo;
                                    l.autore = autore;
                                    l.anno = anno;
                                    l.genere = genere;
                                    l.descrizione = descrizione;
                                    libri.add(l);
                                }
                            }
                        } catch (Exception e) {
                            // Ignora righe malformate
                        }
                        buffer.setLength(0);
                    }
                }
            }
            String sql = "INSERT INTO Libri(titolo,autore,anno,genere,descrizione) VALUES (?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                int inserted = 0;
                for (Libro l : libri) {
                    ps.setString(1, l.titolo);
                    ps.setString(2, l.autore);
                    if (l.anno == null) ps.setNull(3, Types.INTEGER); else ps.setInt(3, l.anno);
                    ps.setString(4, l.genere);
                    ps.setString(5, l.descrizione);
                    ps.addBatch();
                    inserted++;
                }
                ps.executeBatch();
                System.out.println("[SEED] Import iniziale completato. Libri inseriti: " + inserted);
            }
        }
    }

    // Trova l'indice di una colonna cercando il nome (case-insensitive)
    private static int findColumnIndex(String[] headers, String... names) {
        for (int i = 0; i < headers.length; i++) {
            String h = headers[i].trim().toLowerCase();
            for (String name : names) {
                if (h.contains(name.toLowerCase())) return i;
            }
        }
        return -1;
    }

    // Parse CSV che rispetta i doppi apici
    private static String[] parseCSVLineQuoted(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ';' && !inQuotes) {
                result.add(current.toString());
                current.setLength(0);
            } else if ((c == '\n' || c == '\r') && !inQuotes) {
                // ignora newline fuori dai quotes
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());
        return result.toArray(new String[0]);
    }

    // Conta i semicoloni
    private static int countSemicolons(String s) {
        int count = 0;
        for (char c : s.toCharArray()) {
            if (c == ';') count++;
        }
        return count;
    }

    public static Connection get() { return conn; }
}
