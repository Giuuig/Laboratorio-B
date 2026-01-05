package com.bookrecommender.server.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Import CSV robusto per BooksDatasetClean.csv.
 * - Gestisce campi tra virgolette che contengono il delimitatore ';'
 * - Salta righe non valide
 * Esegui dalla cartella serverBR (o passa path completo come arg[0]).
 */
public class ImportBooksCsv {
    public static List<String> splitLine(String line, char sep) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                // toggle quote state, handle escaped quotes ""
                if (inQuotes && i+1 < line.length() && line.charAt(i+1) == '"') {
                    cur.append('"');
                    i++; // skip escaped
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == sep && !inQuotes) {
                out.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        out.add(cur.toString());
        return out;
    }

    public static void main(String[] args) throws Exception {
        String csvPath = null;
        if (args.length > 0) csvPath = args[0];
        else {
            // default to resource path in project
            csvPath = "src/main/resources/BooksDatasetClean.csv";
        }

        File f = new File(csvPath);
        if (!f.exists()) {
            System.err.println("CSV file not found: " + f.getAbsolutePath());
            return;
        }

        String url = "jdbc:postgresql://localhost:5432/bookrecommender";
        String user = System.getenv().getOrDefault("BR_DB_USER", "postgres");
        String pwd  = System.getenv().getOrDefault("BR_DB_PASSWORD", "postgres");

        try (Connection c = DriverManager.getConnection(url, user, pwd)) {
            c.setAutoCommit(false);

            // remove dummy entry if present
            try (PreparedStatement del = c.prepareStatement("DELETE FROM Libri WHERE titolo = ?")) {
                del.setString(1, "__DUMMY__");
                del.executeUpdate();
            }

            // read csv
            int inserted = 0;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
                String header = br.readLine();
                String line;
                String sql = "INSERT INTO Libri(titolo,autore,anno,genere,descrizione) VALUES (?,?,?,?,?)";
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    while ((line = br.readLine()) != null) {
                        if (line.trim().isEmpty()) continue;
                        List<String> parts = splitLine(line, ';');
                        // Expect at least 6 columns (we need indices 0,1,2,3,5)
                        if (parts.size() < 6) continue;
                        String titolo = parts.get(0).trim();
                        String autore = parts.get(1).trim();
                        String genere = parts.get(2).trim();
                        String descr = parts.get(3).trim();
                        String yearStr = parts.size() > 5 ? parts.get(5).trim() : "";
                        Integer anno = null;
                        if (!yearStr.isEmpty()) {
                            try { anno = Integer.valueOf(yearStr); } catch (NumberFormatException ex) { anno = null; }
                        }

                        ps.setString(1, titolo);
                        ps.setString(2, autore);
                        if (anno == null) ps.setNull(3, java.sql.Types.INTEGER); else ps.setInt(3, anno);
                        ps.setString(4, genere);
                        ps.setString(5, descr);
                        ps.addBatch();
                        inserted++;
                        if (inserted % 500 == 0) ps.executeBatch();
                    }
                    ps.executeBatch();
                }
            }
            c.commit();

            // count rows
            try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Libri")) {
                rs.next();
                int total = rs.getInt(1);
                System.out.println("Import completato. Inseriti: " + inserted + ", Totale Libri: " + total);
            }
        }
    }
}
