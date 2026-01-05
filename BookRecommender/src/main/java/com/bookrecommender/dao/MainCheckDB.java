package com.bookrecommender.dao;

import java.sql.*;

public class MainCheckDB {
    public static void main(String[] args) throws Exception {
        Connection c = DBManager.getConnection();
        try (Statement st = c.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM libri");
            rs.next();
            System.out.println(">>> Libri nel DB: " + rs.getInt(1));
        }
    }
}
