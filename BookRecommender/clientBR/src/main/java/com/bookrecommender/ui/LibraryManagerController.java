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

import com.bookrecommender.ui.RecommendationsDialogController;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller per la gestione delle librerie dell'utente.
 * Supporta: REGISTRA_LIBRERIA, LISTA_LIBRERIE, AGGIUNGI_LIBRI_A_LIBRERIA
 */
public class LibraryManagerController {

    @FXML private ComboBox<String> librariesCombo;
    @FXML private TextField newLibraryName;
    @FXML private TextField searchBookField;
    @FXML private TableView<Libro> booksAvailableTable;
    @FXML private TableColumn<Libro,String> colTitolo;
    @FXML private TableColumn<Libro,String> colAutore;
    @FXML private TableColumn<Libro,Integer> colAnno;
    @FXML private TableColumn<Libro,String> colGenere;
    @FXML private TableColumn<Libro,String> colDescrizione;
    @FXML private Label statusLabel;

    private int currentUserId;
    private final Gson gson = new Gson();
    private Map<String, Integer> librariesMap = new HashMap<>();

    public void setContext(int currentUserId) {
        this.currentUserId = currentUserId;
        if (currentUserId > 0) {
            loadLibraries();
            // Load books from first library if available
            if (!librariesCombo.getItems().isEmpty()) {
                librariesCombo.getSelectionModel().selectFirst();
                onViewLibraryBooks();
            }
        }
    }

    @FXML
    public void initialize() {
        if (colTitolo != null) {
            colTitolo.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        }
        if (colAutore != null) {
            colAutore.setCellValueFactory(new PropertyValueFactory<>("autore"));
        }
        if (colAnno != null) {
            colAnno.setCellValueFactory(cell -> {
                Integer anno = cell.getValue().getAnno();
                return new SimpleIntegerProperty(anno == null ? 0 : anno).asObject();
            });
        }
        if (colGenere != null) {
            colGenere.setCellValueFactory(new PropertyValueFactory<>("genere"));
        }
        if (colDescrizione != null) {
            colDescrizione.setCellValueFactory(new PropertyValueFactory<>("descrizione"));
        }
        if (booksAvailableTable != null) {
            booksAvailableTable.setPlaceholder(new Label("Cerca libri da aggiungere"));
            booksAvailableTable.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
            booksAvailableTable.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    onOpenBookDetails();
                }
            });
        }
    }

    private void setStatus(String msg) {
        statusLabel.setText(msg == null ? "" : msg);
    }

    @FXML
    public void onCreateLibrary() {
        showLibraryCreationDialog();
    }



    private List<Libro> currentLibraryBooks = new ArrayList<>();
    
    @FXML
    public void onLibrarySelected() {
        onViewLibraryBooks();
    }
    
    @FXML
    public void onSearchInLibrary() {
        if (currentLibraryBooks.isEmpty()) {
            showAlert("Info", "Nessun libro nella libreria selezionata");
            return;
        }
        
        String query = searchBookField.getText().trim().toLowerCase();
        
        if (query.isEmpty()) {
            // Mostra tutti i libri se la ricerca è vuota
            booksAvailableTable.setItems(FXCollections.observableArrayList(currentLibraryBooks));
            setStatus("Libri nella libreria: " + currentLibraryBooks.size());
            return;
        }
        
        // Filtra i libri della libreria corrente
        List<Libro> filtered = new ArrayList<>();
        for (Libro libro : currentLibraryBooks) {
            if ((libro.getTitolo() != null && libro.getTitolo().toLowerCase().contains(query)) ||
                (libro.getAutore() != null && libro.getAutore().toLowerCase().contains(query)) ||
                (libro.getGenere() != null && libro.getGenere().toLowerCase().contains(query))) {
                filtered.add(libro);
            }
        }
        
        booksAvailableTable.setItems(FXCollections.observableArrayList(filtered));
        setStatus("Trovati " + filtered.size() + " libri nella libreria");
    }
    
    @FXML
    public void onViewLibraryBooks() {
        String selected = librariesCombo.getValue();
        if (selected == null) {
            showAlert("Errore", "Selezionare una libreria");
            return;
        }
        
        Integer libreriaId = librariesMap.get(selected);
        Map<String,Object> p = new HashMap<>();
        p.put("libreriaId", libreriaId);
        
        try (ClientConnection conn = new ClientConnection()) {
            Response r = conn.send("LIBRI_IN_LIBRERIA", p);
            if (r.ok) {
                Type listType = new TypeToken<List<Libro>>(){}.getType();
                currentLibraryBooks = gson.fromJson(gson.toJson(r.data.get("libri")), listType);
                booksAvailableTable.setItems(FXCollections.observableArrayList(currentLibraryBooks));
                searchBookField.clear();
                setStatus("Libri in libreria " + selected + ": " + currentLibraryBooks.size());
            } else {
                showAlert("Errore", r.message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Errore", "Errore di comunicazione: " + e.getMessage());
        }
    }

    @FXML
    public void onOpenBookDetails() {
        Libro libro = booksAvailableTable.getSelectionModel().getSelectedItem();
        if (libro == null) {
            showAlert("Errore", "Seleziona un libro");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/book-details.fxml"));
            javafx.scene.Parent root = loader.load();
            BookDetailsController ctrl = loader.getController();
            ctrl.setContext(libro, currentUserId);

            Stage stage = new Stage();
            stage.setTitle("Dettagli libro");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root, 750, 650));
            stage.setMinWidth(750);
            stage.setMinHeight(650);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Errore", "Impossibile aprire i dettagli");
        }
    }

    @FXML
    public void onOpenRecommendations() {
        Libro libro = booksAvailableTable.getSelectionModel().getSelectedItem();
        if (libro == null) {
            showAlert("Errore", "Seleziona un libro");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/recommendations-dialog.fxml"));
            javafx.scene.Parent root = loader.load();
            RecommendationsDialogController ctrl = loader.getController();
            ctrl.setContext(libro, currentUserId);

            Stage stage = new Stage();
            stage.setTitle("Suggerisci libri");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root, 900, 700));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Errore", "Impossibile aprire suggerimenti");
        }
    }

    @FXML
    public void onQuickCreateLibrary() {
        String name = newLibraryName.getText().trim();
        if (name.isEmpty()) {
            showAlert("Errore", "Inserire nome libreria");
            return;
        }
        createLibrary(name);
    }

    private void loadLibraries() {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUserId);

        try (ClientConnection conn = new ClientConnection()) {
            Response r = conn.send("LISTA_LIBRERIE", params);
            if (r.ok) {
                // La risposta dovrebbe contenere una mappa id -> nome
                Type mapType = new TypeToken<Map<Integer, String>>(){}.getType();
                Map<Integer, String> librerieMap = gson.fromJson(
                        gson.toJson(r.data.get("librerie")),
                        mapType
                );
                
                librariesMap.clear();
                List<String> names = new ArrayList<>();
                for (Map.Entry<Integer, String> entry : librerieMap.entrySet()) {
                    names.add(entry.getValue());
                    librariesMap.put(entry.getValue(), entry.getKey());
                }
                librariesCombo.getItems().setAll(names);
                setStatus("Librerie caricate: " + names.size());
            } else {
                showAlert("Errore", r.message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Errore", "Impossibile caricare le librerie: " + e.getMessage());
        }
    }

    private void createLibrary(String name) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUserId);
        params.put("nome", name);

        try (ClientConnection conn = new ClientConnection()) {
            Response r = conn.send("REGISTRA_LIBRERIA", params);
            if (r.ok) {
                showAlert("Successo", "Libreria creata");
                newLibraryName.clear();
                loadLibraries();
            } else {
                showAlert("Errore", r.message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Errore", "Impossibile creare la libreria: " + e.getMessage());
        }
    }

    private void showLibraryCreationDialog() {
        // Semplice dialog con TextField per il nome
        javafx.scene.control.Dialog<String> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Crea Libreria");
        dialog.setHeaderText("Inserisci il nome della nuova libreria");

        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(10);
        TextField libraryNameField = new TextField();
        libraryNameField.setPromptText("Nome libreria");
        content.getChildren().add(libraryNameField);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        var result = dialog.showAndWait();
        if (result.isPresent() && !libraryNameField.getText().trim().isEmpty()) {
            createLibrary(libraryNameField.getText().trim());
        }
    }

    private void showBooksSelectionDialog(String libraryName) {
        // Placeholder: mostra dialog per selezionare libri
        showAlert("Info", "Aggiunta libri a: " + libraryName);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }

    @FXML
    public void onSearchAndAddBooks() {
        String selectedLibrary = librariesCombo.getValue();
        if (selectedLibrary == null || selectedLibrary.trim().isEmpty()) {
            setStatus("Seleziona prima una libreria");
            return;
        }

        Integer libraryId = librariesMap.get(selectedLibrary);
        if (libraryId == null) {
            setStatus("Libreria non valida");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/search-add-books.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600);
            
            SearchAddBooksController controller = loader.getController();
            controller.setContext(currentUserId, libraryId, selectedLibrary);
            
            Stage stage = new Stage();
            stage.setTitle("Cerca e aggiungi libri a: " + selectedLibrary);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            // Refresh library books after adding
            onViewLibraryBooks();
        } catch (Exception e) {
            e.printStackTrace();
            setStatus("Errore apertura ricerca libri");
        }
    }
}
