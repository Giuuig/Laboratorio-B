
package com.bookrecommender.service;

import com.bookrecommender.dao.UserDAO;
import com.bookrecommender.model.UtenteRegistrato;

public class UserService {
    private final UserDAO userDAO = new UserDAO();

    public UtenteRegistrato register(String nome, String cognome, String email, String password){
        try {
            String hash = Integer.toHexString(password.hashCode());
            return userDAO.create(nome, cognome, email, hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean login(String email, String password){
        try {
            String hash = Integer.toHexString(password.hashCode());
            return userDAO.checkPassword(email, hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
