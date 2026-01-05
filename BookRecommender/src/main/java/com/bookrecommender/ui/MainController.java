
package com.bookrecommender.ui;

import com.bookrecommender.client.ClientConnection;
import com.bookrecommender.model.Libro;
import com.bookrecommender.net.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainController {
    @FXML private TextField emailField, passwordField, titoloField, autoreField, annoField, libroIdField, votoField, commentoField;
    @FXML private Label statusLabel;
    @FXML private TableView<Libro> table;
    @FXML private TableColumn<Libro, String> colTitolo;
    @FXML private TableColumn<Libro, String> colAutore;
    @FXML private TableColumn<Libro, Integer> colAnno;

    private final Gson gson = new Gson();
    private ClientConnection conn;
    private boolean logged = false;
    private int currentUserId = 1; // demo: could be improved to fetch real id

    @FXML
    public void initialize(){
        colTitolo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTitolo()));
        colAutore.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getAutore()));
        colAnno.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getAnno()));
        try {
            conn = new ClientConnection();
            setStatus("Connessione al server OK");
        } catch (Exception e){
            setStatus("Server non raggiungibile: " + e.getMessage());
        }
    }

    private void setStatus(String s){ statusLabel.setText(s); }

    public void onLogin(){
        Map<String,String> p = new HashMap<>();
        p.put("email", emailField.getText());
        p.put("password", passwordField.getText());
        Response r = conn.send("LOGIN", p);
        if (r.ok){ logged = true; setStatus("Login OK"); }
        else setStatus(r.message);
    }

    public void onRegister(){
        Map<String,String> p = new HashMap<>();
        p.put("nome", "Utente");
        p.put("cognome", "Demo");
        p.put("email", emailField.getText());
        p.put("password", passwordField.getText());
        Response r = conn.send("REGISTER", p);
        if (r.ok){ logged = true; setStatus("Registrazione OK"); }
        else setStatus(r.message);
    }

    public void onSearch(){
        Map<String,String> p = new HashMap<>();
        p.put("titolo", titoloField.getText());
        p.put("autore", autoreField.getText());
        p.put("anno", annoField.getText());
        Response r = conn.send("SEARCH_BOOKS", p);
        if (r.ok){
            Type listType = new TypeToken<List<Libro>>(){}.getType();
            List<Libro> list = gson.fromJson(r.json, listType);
            table.setItems(FXCollections.observableArrayList(list));
            setStatus("Trovati " + list.size() + " libri");
        } else setStatus(r.message);
    }

    public void onRate(){
        if (!logged){ setStatus("Effettua il login"); return; }
        Map<String,String> p = new HashMap<>();
        p.put("userId", String.valueOf(currentUserId));
        p.put("libroId", libroIdField.getText());
        p.put("voto", votoField.getText());
        p.put("commento", commentoField.getText());
        Response r = conn.send("RATE_BOOK", p);
        if (r.ok) setStatus("Valutazione salvata");
        else setStatus(r.message);
    }
}
