package com.bookrecommender.ui;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.bookrecommender.client.ClientConnection;
import com.bookrecommender.common.Response;
import com.bookrecommender.model.Libro;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * Controller per la ricerca e aggiunta di libri a una libreria
 */
public class SearchAddBooksController {

    @FXML private Label libraryNameLabel;
    @FXML private TextField searchTitolo;
    @FXML private TextField searchAutore;
    @FXML private TextField searchAnno;
    @FXML private TableView<Libro> booksTable;
    @FXML private TableColumn<Libro,String> colTitolo;
    @FXML private TableColumn<Libro,String> colAutore;
    @FXML private TableColumn<Libro,Integer> colAnno;
    @FXML private TableColumn<Libro,String> colGenere;
    @FXML private TableColumn<Libro,String> colDescrizione;
    @FXML private Label statusLabel;

    private int currentUserId;
    private int libraryId;
    private String libraryName;
    private final Gson gson = new Gson();

    public void setContext(int currentUserId, int libraryId, String libraryName) {
        this.currentUserId = currentUserId;
        this.libraryId = libraryId;
        this.libraryName = libraryName;
        libraryNameLabel.setText("Aggiungi libri a: " + libraryName);
    }

    @FXML
    public void initialize() {
        colTitolo.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        colAutore.setCellValueFactory(new PropertyValueFactory<>("autore"));
        colAnno.setCellValueFactory(cell -> {
            Integer anno = cell.getValue().getAnno();
            return new SimpleIntegerProperty(anno == null ? 0 : anno).asObject();
        });
        colGenere.setCellValueFactory(new PropertyValueFactory<>("genere"));
        colDescrizione.setCellValueFactory(new PropertyValueFactory<>("descrizione"));
        
        booksTable.setPlaceholder(new Label(""));
        booksTable.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
    }

    private void setStatus(String msg) {
        statusLabel.setText(msg == null ? "" : msg);
    }

    private void loadAllBooks() {
        Map<String,Object> p = new HashMap<>();
        p.put("titolo", "");
        p.put("autore", "");
        p.put("anno", "");

        try (ClientConnection conn = new ClientConnection()) {
            Response r = conn.send("SEARCH_BOOKS_ADVANCED", p);
            if (r.ok) {
                Type listType = new TypeToken<List<Libro>>(){}.getType();
                List<Libro> libri = gson.fromJson(gson.toJson(r.data.get("libri")), listType);
                booksTable.setItems(FXCollections.observableArrayList(libri));
                setStatus("Trovati " + libri.size() + " libri");
            } else {
                setStatus(r.message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            setStatus("Errore di comunicazione: " + e.getMessage());
        }
    }

    @FXML
    public void onSearchByTitle() {
        String titolo = searchTitolo.getText().trim();
        if (titolo.isEmpty()) {
            loadAllBooks();
            return;
        }
        
        Map<String,Object> p = new HashMap<>();
        p.put("titolo", titolo);
        p.put("autore", "");
        p.put("anno", "");

        searchBooks(p);
    }

    @FXML
    public void onSearchByAuthor() {
        String autore = searchAutore.getText().trim();
        if (autore.isEmpty()) {
            loadAllBooks();
            return;
        }
        
        Map<String,Object> p = new HashMap<>();
        p.put("titolo", "");
        p.put("autore", autore);
        p.put("anno", "");

        searchBooks(p);
    }

    @FXML
    public void onSearchByAuthorAndYear() {
        String autore = searchAutore.getText().trim();
        String anno = searchAnno.getText().trim();
        
        if (autore.isEmpty() && anno.isEmpty()) {
            loadAllBooks();
            return;
        }
        
        Map<String,Object> p = new HashMap<>();
        p.put("titolo", "");
        p.put("autore", autore);
        p.put("anno", anno);

        searchBooks(p);
    }

    private void searchBooks(Map<String,Object> params) {
        try (ClientConnection conn = new ClientConnection()) {
            Response r = conn.send("SEARCH_BOOKS_ADVANCED", params);
            if (r.ok) {
                Type listType = new TypeToken<List<Libro>>(){}.getType();
                List<Libro> libri = gson.fromJson(gson.toJson(r.data.get("libri")), listType);
                booksTable.setItems(FXCollections.observableArrayList(libri));
                setStatus("Trovati " + libri.size() + " libri");
            } else {
                setStatus(r.message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            setStatus("Errore di comunicazione: " + e.getMessage());
        }
    }

    @FXML
    public void onAddSelectedBooks() {
        List<Libro> selected = booksTable.getSelectionModel().getSelectedItems();
        if (selected.isEmpty()) {
            setStatus("Seleziona almeno un libro");
            return;
        }

        List<Integer> bookIds = selected.stream()
            .map(Libro::getId)
            .collect(Collectors.toList());

        Map<String,Object> p = new HashMap<>();
        p.put("libreriaId", libraryId);
        p.put("libri", bookIds);

        try (ClientConnection conn = new ClientConnection()) {
            Response r = conn.send("AGGIUNGI_LIBRI_A_LIBRERIA", p);
            if (r.ok) {
                setStatus("Aggiunti " + selected.size() + " libri alla libreria");
                showAlert("Successo", "Libri aggiunti con successo!");
            } else {
                setStatus(r.message);
                showAlert("Errore", r.message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            setStatus("Errore di comunicazione: " + e.getMessage());
            showAlert("Errore", "Errore di comunicazione: " + e.getMessage());
        }
    }

    @FXML
    public void onClose() {
        Stage stage = (Stage) booksTable.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
}
