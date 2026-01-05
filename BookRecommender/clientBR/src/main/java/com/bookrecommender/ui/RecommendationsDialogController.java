package com.bookrecommender.ui;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * Controller per il dialog di suggerimento libri.
 * Consente di suggerire fino a 3 libri per il libro selezionato.
 * Supporta: INSERISCI_SUGGERIMENTO_LIBRO
 */
public class RecommendationsDialogController {

    @FXML private Label selectedBookTitle;
    @FXML private Label selectedBookAuthor;

    @FXML private RadioButton slot1;
    @FXML private RadioButton slot2;
    @FXML private RadioButton slot3;
    @FXML private ToggleGroup slotToggle;
    @FXML private Label slotLabel1;
    @FXML private Label slotLabel2;
    @FXML private Label slotLabel3;

    @FXML private TextField bookSearchField;
    @FXML private TableView<Libro> booksTable;
    @FXML private TableColumn<Libro, String> colTitolo;
    @FXML private TableColumn<Libro, String> colAutore;
    @FXML private TableColumn<Libro, Integer> colAnno;

    private Libro selectedBook;
    private int currentUserId;
    private List<Libro> allBooks = new ArrayList<>();
    private Libro slotSel1;
    private Libro slotSel2;
    private Libro slotSel3;
    private final Gson gson = new Gson();

    public void setContext(Libro libro, int currentUserId) {
        this.selectedBook = libro;
        this.currentUserId = currentUserId;

        if (libro != null) {
            selectedBookTitle.setText(libro.getTitolo());
            selectedBookAuthor.setText(libro.getAutore());
        }

        initTableColumns();
        loadAllBooks();
        updateSlotLabels();
    }

    private void initTableColumns() {
        colTitolo.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        colAutore.setCellValueFactory(new PropertyValueFactory<>("autore"));
        colAnno.setCellValueFactory(cell -> new SimpleIntegerProperty(
                cell.getValue().getAnno() == null ? 0 : cell.getValue().getAnno()).asObject());
    }

    private void loadAllBooks() {
        Map<String, Object> params = new HashMap<>();
        params.put("q", "");

        try (ClientConnection conn = new ClientConnection()) {
            Response r = conn.send("SEARCH_BOOKS", params);
            if (r.ok) {
                Type listType = new TypeToken<List<Libro>>(){}.getType();
                allBooks = gson.fromJson(
                        gson.toJson(r.data.get("libri")),
                        listType
                );
                updateBooksDisplay(allBooks);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onSearchBooks() {
        String query = bookSearchField.getText().trim();
        Map<String, Object> params = new HashMap<>();
        params.put("q", query);

        try (ClientConnection conn = new ClientConnection()) {
            Response r = conn.send("SEARCH_BOOKS", params);
            if (r.ok) {
                Type listType = new TypeToken<List<Libro>>(){}.getType();
                List<Libro> books = gson.fromJson(
                        gson.toJson(r.data.get("libri")),
                        listType
                );
                updateBooksDisplay(books);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateBooksDisplay(List<Libro> books) {
        booksTable.setItems(FXCollections.observableArrayList(books));
    }

    private void updateSlotLabels() {
        slotLabel1.setText(slotSel1 == null ? "(vuoto)" : slotSel1.getTitolo());
        slotLabel2.setText(slotSel2 == null ? "(vuoto)" : slotSel2.getTitolo());
        slotLabel3.setText(slotSel3 == null ? "(vuoto)" : slotSel3.getTitolo());
    }

    @FXML
    public void onAssignToSlot() {
        Libro libro = booksTable.getSelectionModel().getSelectedItem();
        if (libro == null) {
            showAlert("Errore", "Seleziona un libro dalla tabella");
            return;
        }

        RadioButton selected = (RadioButton) slotToggle.getSelectedToggle();
        if (selected == null) {
            showAlert("Errore", "Seleziona uno slot");
            return;
        }

        if (selected == slot1) {
            slotSel1 = libro;
        } else if (selected == slot2) {
            slotSel2 = libro;
        } else if (selected == slot3) {
            slotSel3 = libro;
        }
        updateSlotLabels();
    }

    @FXML
    public void onSaveRecommendations() {
        if (selectedBook == null || currentUserId <= 0) {
            showAlert("Errore", "Contesto non valido");
            return;
        }

        List<Integer> recommendations = new ArrayList<>();
        if (slotSel1 != null) recommendations.add(slotSel1.getId());
        if (slotSel2 != null) recommendations.add(slotSel2.getId());
        if (slotSel3 != null) recommendations.add(slotSel3.getId());

        // rimuovi duplicati
        recommendations = new ArrayList<>(new java.util.LinkedHashSet<>(recommendations));

        // evita di suggerire lo stesso libro
        recommendations.removeIf(id -> id == selectedBook.getId());

        if (recommendations.isEmpty()) {
            showAlert("Errore", "Seleziona almeno un libro diverso da quello di partenza");
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUserId);
        params.put("libroId", selectedBook.getId());
        params.put("suggeriti", recommendations);

        try (ClientConnection conn = new ClientConnection()) {
            Response r = conn.send("INSERISCI_SUGGERIMENTO_LIBRO", params);
            if (!r.ok) {
                showAlert("Errore", r.message);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Errore", "Errore di comunicazione: " + e.getMessage());
            return;
        }

        showAlert("Successo", "Suggerimenti salvati");
        closeDialog();
    }

    @FXML
    public void onCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) selectedBookTitle.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
}
