package com.bookrecommender.server.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CreateDB {
    public static void main(String[] args) throws Exception {
        String host = "localhost";
        String port = "5432";
        String adminDb = "postgres";
        String dbName = "bookrecommender";
        String user = System.getenv().getOrDefault("BR_DB_USER", "postgres");
        String pwd  = System.getenv().getOrDefault("BR_DB_PASSWORD", "postgres");

        String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, adminDb);
        System.out.println("Connecting to: " + url + " as user=" + user);
        try (Connection c = DriverManager.getConnection(url, user, pwd);
             Statement st = c.createStatement()) {
            System.out.println("Dropping database if exists: " + dbName);
            // Terminate connections and drop/create
            try {
                st.executeUpdate("DROP DATABASE IF EXISTS \"" + dbName + "\";");
            } catch (Exception e){
                System.out.println("Warning while dropping DB: " + e.getMessage());
            }
            System.out.println("Creating database: " + dbName);
            st.executeUpdate("CREATE DATABASE \"" + dbName + "\";");
            System.out.println("Database created successfully.");
        }
    }
}
