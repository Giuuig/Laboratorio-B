
package com.bookrecommender.service;

import com.bookrecommender.dao.BookDAO;
import com.bookrecommender.model.Libro;
import java.util.List;

public class BookService {
    private final BookDAO bookDAO = new BookDAO();

    public List<Libro> search(String titolo, String autore, Integer anno){
        return bookDAO.search(titolo, autore, anno);
    }
}
