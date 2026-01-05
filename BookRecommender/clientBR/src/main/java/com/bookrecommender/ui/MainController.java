package com.bookrecommender.ui;

import java.io.IOException;
import java.lang.reflect.Type;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller principale per la GUI.
 * Adattato per usare com.bookrecommender.common.Response (BookRecommenderPatched_full).
 */
public class MainController {

    @FXML private TableView<Libro> advSearchTable;
    @FXML private TableColumn<Libro,String> advColTitolo;
    @FXML private TableColumn<Libro,String> advColAutore;
    @FXML private TableColumn<Libro,Integer> advColAnno;
    @FXML private TableColumn<Libro,String> advColGenere;
    @FXML private TableColumn<Libro,String> advColDescrizione;

    @FXML private TextField advSearchTitolo;
    @FXML private TextField advSearchAutore;
    @FXML private TextField advSearchAnno;
    @FXML private Label statusLabel;
    @FXML private Button gestisciLibrerieBtn;

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML private TextField NomeField;
    @FXML private TextField CognomeField;
    /*@FXML private TextField regEmailField;*/
    /*@FXML private TextField regUseridField;*/
    /*@FXML private PasswordField regPasswordField;*/

    private final Gson gson = new Gson();
    private int currentUserId = 0;
    private String currentUserName = "";

    @FXML
    public void initialize(){
        // Setup colonne tabella libri - Ricerca
        advColTitolo.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        advColAutore.setCellValueFactory(new PropertyValueFactory<>("autore"));
        advColAnno.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getAnno() == null ? 0 : cell.getValue().getAnno()).asObject());
        advColGenere.setCellValueFactory(new PropertyValueFactory<>("genere"));
        advColDescrizione.setCellValueFactory(new PropertyValueFactory<>("descrizione"));

        // Carica tutti i libri all'avvio (richiesta utente)
        loadAllBooksAdvanced();

        // Nessun placeholder "No content": tabella vuota = semplicemente vuota
        advSearchTable.setPlaceholder(new Label(""));
        
        // Nascondi gestisci librerie se non loggato
        if (gestisciLibrerieBtn != null) {
            gestisciLibrerieBtn.setVisible(false);
        }

        // Doppio click su riga = apri dettagli
        advSearchTable.setRowFactory(tv -> {
            TableRow<Libro> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openBookDetails(row.getItem());
                }
            });
            return row;
        });

        // Doppio click su riga = apri dettagli (Tab Ricerca Avanzata)
        advSearchTable.setRowFactory(tv -> {
            TableRow<Libro> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openBookDetails(row.getItem());
                }
            });
            return row;
        });
    }

    private void setStatus(String msg){
        statusLabel.setText(msg == null ? "" : msg);
    }

    // ====== LOGIN & REGISTRAZIONE ==================================================

    @FXML
    public void onOpenLoginDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Login / Registrazione");
        dialog.setHeaderText("Accedi o registrati al sistema");
        dialog.initModality(Modality.APPLICATION_MODAL);

        VBox content = new VBox(15);
        content.setStyle("-fx-padding:20;");
        content.setPrefWidth(550);
        content.setPrefHeight(450);

        Label modeLabel = new Label("Modalità: Login");
        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome");
        nomeField.setVisible(false);
        nomeField.setManaged(false);

        TextField cognomeField = new TextField();
        cognomeField.setPromptText("Cognome");
        cognomeField.setVisible(false);
        cognomeField.setManaged(false);

        TextField codiceFiscaleField = new TextField();
        codiceFiscaleField.setPromptText("Codice Fiscale");
        codiceFiscaleField.setVisible(false);
        codiceFiscaleField.setManaged(false);

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField useridField = new TextField();
        useridField.setPromptText("UserID");
        useridField.setVisible(false);
        useridField.setManaged(false);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        javafx.scene.control.Button toggleButton = new javafx.scene.control.Button("Vai a Registrazione");
        toggleButton.setPrefHeight(45);
        toggleButton.setPrefWidth(500);
        toggleButton.setOnAction(e -> {
            boolean isLoginMode = "Modalità: Login".equals(modeLabel.getText());
            if (isLoginMode) {
                modeLabel.setText("Modalità: Registrazione");
                nomeField.setVisible(true);
                nomeField.setManaged(true);
                cognomeField.setVisible(true);
                cognomeField.setManaged(true);
                codiceFiscaleField.setVisible(true);
                codiceFiscaleField.setManaged(true);
                useridField.setVisible(true);
                useridField.setManaged(true);
                toggleButton.setText("Vai a Login");
            } else {
                modeLabel.setText("Modalità: Login");
                nomeField.setVisible(false);
                nomeField.setManaged(false);
                cognomeField.setVisible(false);
                cognomeField.setManaged(false);
                codiceFiscaleField.setVisible(false);
                codiceFiscaleField.setManaged(false);
                useridField.setVisible(false);
                useridField.setManaged(false);
                toggleButton.setText("Vai a Registrazione");
            }
        });

        content.getChildren().addAll(
            modeLabel,
            nomeField,
            cognomeField,
            codiceFiscaleField,
            emailField,
            useridField,
            passwordField,
            toggleButton
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setMinWidth(600);
        dialog.getDialogPane().setMinHeight(500);
        dialog.getDialogPane().setPrefWidth(600);
        dialog.getDialogPane().setPrefHeight(500);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(button -> button);

        var result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean isLoginMode = "Modalità: Login".equals(modeLabel.getText());
            if (isLoginMode) {
                performLogin(useridField.getText().isEmpty() ? emailField.getText() : useridField.getText(), passwordField.getText());
            } else {
                performRegister(nomeField.getText(), cognomeField.getText(), codiceFiscaleField.getText(), 
                              emailField.getText(), useridField.getText(), passwordField.getText());
            }
        }
    }

    private void performLogin(String userid, String password) {
        setStatus("");
        Map<String,Object> p = new HashMap<>();
        p.put("userid", userid);
        p.put("password", password);

        try (ClientConnection conn = new ClientConnection()){
            Response r = conn.send("LOGIN", p);
            if (r.ok){
                try {
                    currentUserId = ((Number) r.data.get("userId")).intValue();
                    currentUserName = r.data.get("nome") != null ? r.data.get("nome").toString() : "";
                } catch (Exception ignored){
                    currentUserId = 0;
                    currentUserName = "";
                }
                setStatus("Ciao " + currentUserName + "!");
                showAlert("Successo", "Accesso eseguito con successo!");
                if (gestisciLibrerieBtn != null) {
                    gestisciLibrerieBtn.setVisible(true);
                }
            } else {
                setStatus(r.message);
                showAlert("Errore", r.message);
            }
        } catch (Exception e){
            e.printStackTrace();
            setStatus("Errore di comunicazione: " + e.getMessage());
            showAlert("Errore", "Errore di comunicazione: " + e.getMessage());
        }
    }

    private void performRegister(String nome, String cognome, String codiceFiscale, 
                                  String email, String userid, String password) {
        setStatus("");
        Map<String,Object> p = new HashMap<>();
        p.put("nome", nome);
        p.put("cognome", cognome);
        p.put("codiceFiscale", codiceFiscale);
        p.put("email", email);
        p.put("userid", userid);
        p.put("password", password);

        try (ClientConnection conn = new ClientConnection()){
            Response r = conn.send("REGISTER", p);
            if (r.ok) {
                setStatus("Registrazione riuscita! Effettua il login.");
                showAlert("Successo", "Registrazione completata! Accedi con le tue credenziali.");
            } else {
                setStatus(r.message);
                showAlert("Errore", r.message);
            }
        } catch (Exception e){
            e.printStackTrace();
            setStatus("Errore di comunicazione: " + e.getMessage());
            showAlert("Errore", "Errore di comunicazione: " + e.getMessage());
        }
    }

    @FXML
    public void onLogin(){
        // Kept for compatibility; now called from dialog
    }

    @FXML
    public void onRegister(){
        // Kept for compatibility; now called from dialog
    }

    // ====== LOGOUT ================================================================

    @FXML
    public void onLogout() {
        // azzera l'utente loggato
        currentUserId = 0;
        currentUserName = "";

        // pulisci i campi login (opzionale ma carino)
        if (emailField != null)  emailField.clear();
        if (passwordField != null) passwordField.clear();

        // eventuale messaggio di stato
        setStatus("Logout effettuato");
        
        if (gestisciLibrerieBtn != null) {
            gestisciLibrerieBtn.setVisible(false);
        }
    }

    // ====== DETTAGLI LIBRO =========================================================
    @FXML
    public void onOpenAdvDetails() {
        if (advSearchTable == null) return;

        Libro sel = advSearchTable.getSelectionModel().getSelectedItem();
        if (sel != null) {
            openBookDetails(sel);
        } else {
            setStatus("Seleziona prima un libro");
        }
    }

    @FXML
    public void onOpenLibraryManager() {
        if (currentUserId <= 0) {
            setStatus("Devi fare login prima");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/library-manager.fxml"));
            javafx.scene.Parent root = loader.load();
            LibraryManagerController ctrl = loader.getController();
            ctrl.setContext(currentUserId);

            Stage stage = new Stage();
            stage.setTitle("Gestione Librerie");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root, 750, 600));
            stage.setMinWidth(750);
            stage.setMinHeight(600);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            setStatus("Impossibile aprire gestione librerie");
        }
    }


    // ====== RICERCA LIBRI ==========================================================

    @FXML
    public void onCercaTitolo() {
        String titolo = advSearchTitolo.getText().trim();
        if (titolo.isEmpty()) {
            // Campo vuoto = ricarica tutti i libri
            loadAllBooksAdvanced();
            return;
        }
        // Il server si aspetta il parametro "q" per CERCA_TITOLO
        performAdvancedSearch("CERCA_TITOLO", "q", titolo);
    }

    @FXML
    public void onCercaAutore() {
        String autore = advSearchAutore.getText().trim();
        if (autore.isEmpty()) {
            // Campo vuoto = ricarica tutti i libri
            loadAllBooksAdvanced();
            return;
        }
        performAdvancedSearch("CERCA_AUTORE", "autore", autore);
    }

    @FXML
    public void onCercaAutoreAnno() {
        String autore = advSearchAutore.getText().trim();
        String anno = advSearchAnno.getText().trim();
        if (autore.isEmpty() || anno.isEmpty()) {
            setStatus("Inserire autore e anno");
            return;
        }
        int annoNum;
        try {
            annoNum = Integer.parseInt(anno);
        } catch (NumberFormatException nfe) {
            setStatus("Anno non valido");
            return;
        }

        Map<String,Object> p = new HashMap<>();
        p.put("autore", autore);
        p.put("anno", annoNum);

        // Reuse async handler to keep UI responsive
        new Thread(() -> {
            try (ClientConnection conn = new ClientConnection()){
                Response r = conn.send("CERCA_AUTORE_ANNO", p);
                javafx.application.Platform.runLater(() -> {
                    if (r.ok){
                        Type listType = new TypeToken<List<Libro>>(){}.getType();
                        List<Libro> libri = gson.fromJson(
                                gson.toJson(r.data.get("libri")),
                                listType
                        );
                        advSearchTable.setItems(FXCollections.observableArrayList(libri));
                        setStatus("Trovati " + libri.size() + " libri");
                    } else {
                        setStatus(r.message);
                    }
                });
            } catch (Exception e){
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> setStatus("Errore di comunicazione: " + e.getMessage()));
            }
        }).start();
    }

    private void performAdvancedSearch(String requestType, String paramName, String paramValue) {
        setStatus("Ricerca in corso...");
        Map<String,Object> p = new HashMap<>();
        p.put(paramName, paramValue);

        // Run search in background thread to avoid UI freezing
        new Thread(() -> {
            try (ClientConnection conn = new ClientConnection()){
                Response r = conn.send(requestType, p);
                javafx.application.Platform.runLater(() -> {
                    if (r.ok){
                        Type listType = new TypeToken<List<Libro>>(){}.getType();
                        List<Libro> libri = gson.fromJson(
                                gson.toJson(r.data.get("libri")),
                                listType
                        );
                        advSearchTable.setItems(FXCollections.observableArrayList(libri));
                        setStatus("Trovati " + libri.size() + " libri");
                    } else {
                        setStatus(r.message);
                    }
                });
            } catch (Exception e){
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    setStatus("Errore di comunicazione: " + e.getMessage());
                });
            }
        }).start();
    }

    private void loadAllBooksAdvanced() {
        setStatus("Carico tutti i libri ...");
        Map<String,Object> p = new HashMap<>();
        p.put("q", "");

        new Thread(() -> {
            try (ClientConnection conn = new ClientConnection()){
                Response r = conn.send("SEARCH_BOOKS", p);
                javafx.application.Platform.runLater(() -> {
                    if (r.ok){
                        Type listType = new TypeToken<List<Libro>>(){}.getType();
                        List<Libro> libri = gson.fromJson(
                                gson.toJson(r.data.get("libri")),
                                listType
                        );
                        advSearchTable.setItems(FXCollections.observableArrayList(libri));
                        setStatus("Trovati " + libri.size() + " libri");
                    } else {
                        setStatus(r.message);
                    }
                });
            } catch (Exception e){
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> setStatus("Errore di comunicazione: " + e.getMessage()));
            }
        }).start();
    }


    // ====== DETTAGLI LIBRO =========================================================

    private void openBookDetails(Libro sel){
        if (sel == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/book-details.fxml"));
            javafx.scene.Parent root = loader.load();
            BookDetailsController ctrl = loader.getController();
            ctrl.setContext(sel, currentUserId);

            Stage stage = new Stage();
            stage.setTitle("Dettagli libro");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root, 750, 650));
            stage.setMinWidth(750);
            stage.setMinHeight(650);
            stage.setResizable(true);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            setStatus("Impossibile aprire i dettagli");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }

}
