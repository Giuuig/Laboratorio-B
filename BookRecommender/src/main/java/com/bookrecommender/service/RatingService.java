
package com.bookrecommender.service;

import com.bookrecommender.dao.RatingDAO;

public class RatingService {
    private final RatingDAO ratingDAO = new RatingDAO();

    public boolean rate(int userId, int libroId, int voto, String commento){
        try { return ratingDAO.rate(userId, libroId, voto, commento); }
        catch (Exception e){ throw new RuntimeException(e); }
    }
}
