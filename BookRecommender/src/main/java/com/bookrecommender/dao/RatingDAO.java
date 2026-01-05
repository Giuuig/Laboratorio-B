
package com.bookrecommender.dao;

import java.sql.*;

public class RatingDAO {
    public boolean rate(int userId, int libroId, int voto, String commento) throws SQLException {
        String q = "INSERT INTO valutazioni_libri(user_id, libro_id, voto, commento) VALUES (?,?,?,?) " +
                "ON CONFLICT(user_id,libro_id) DO UPDATE SET voto=EXCLUDED.voto, commento=EXCLUDED.commento";
        try (PreparedStatement ps = DBManager.getConnection().prepareStatement(q)){
            ps.setInt(1, userId); ps.setInt(2, libroId); ps.setInt(3, voto); ps.setString(4, commento);
            return ps.executeUpdate() > 0;
        }
    }

    public double averageForBook(int libroId) throws SQLException {
        String q = "SELECT AVG(voto) FROM valutazioni_libri WHERE libro_id=?";
        try (PreparedStatement ps = DBManager.getConnection().prepareStatement(q)){
            ps.setInt(1, libroId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
            return 0.0;
        }
    }
}
