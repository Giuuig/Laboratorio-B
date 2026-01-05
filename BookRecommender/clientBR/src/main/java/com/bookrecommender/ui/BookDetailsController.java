package com.bookrecommender.ui;

import com.bookrecommender.client.ClientConnection;
import com.bookrecommender.model.Libro;
import com.bookrecommender.common.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Finestra di dettaglio libro: mostra info + media voti + recensioni testuali + rating con 5 criteri.
 */
public class BookDetailsController {

    @FXML private Label titleLabel;
    @FXML private Label titoloValue;
    @FXML private Label autoreValue;
    @FXML private Label annoValue;
    @FXML private Label genereValue;
    @FXML private TextArea descrizioneValue;

    @FXML private Label mediaValue;
    @FXML private Label numRecensioniValue;
    @FXML private ListView<String> reviewsList;

    @FXML private TitledPane valutazioneTitledPane;
    
    @FXML private ComboBox<Integer> comboStile;
    @FXML private ComboBox<Integer> comboContenuto;
    @FXML private ComboBox<Integer> comboGradevolezza;
    @FXML private ComboBox<Integer> comboOriginalita;
    @FXML private ComboBox<Integer> comboEdizione;
    
    @FXML private TextArea noteValutazione;

    private Libro libro;
    private int currentUserId;
    private final Gson gson = new Gson();

    @FXML
    public void initialize() {
        // Popola le ComboBox con valori 1-5
        if (comboStile != null) {
            comboStile.getItems().addAll(1, 2, 3, 4, 5);
            comboStile.setValue(3);
        }
        if (comboContenuto != null) {
            comboContenuto.getItems().addAll(1, 2, 3, 4, 5);
            comboContenuto.setValue(3);
        }
        if (comboGradevolezza != null) {
            comboGradevolezza.getItems().addAll(1, 2, 3, 4, 5);
            comboGradevolezza.setValue(3);
        }
        if (comboOriginalita != null) {
            comboOriginalita.getItems().addAll(1, 2, 3, 4, 5);
            comboOriginalita.setValue(3);
        }
        if (comboEdizione != null) {
            comboEdizione.getItems().addAll(1, 2, 3, 4, 5);
            comboEdizione.setValue(3);
        }
    }

    public void setContext(Libro libro, int currentUserId){
        this.libro = libro;
        this.currentUserId = currentUserId;
        if (libro != null){
            titleLabel.setText(libro.getTitolo());
            titoloValue.setText(libro.getTitolo());
            autoreValue.setText(libro.getAutore());
            annoValue.setText(libro.getAnno() == null ? "" : String.valueOf(libro.getAnno()));
            genereValue.setText(libro.getGenere() == null ? "" : libro.getGenere());
            descrizioneValue.setText(libro.getDescrizione() == null ? "" : libro.getDescrizione());
            loadStatsAndReviews();
            updateRatingVisibility();
        }
    }
    
    private void updateRatingVisibility() {
        // Controlla se l'utente può recensire questo libro
        boolean canRate = false;
        
        if (currentUserId > 0 && libro != null) {
            // Verifica se il libro è nelle librerie dell'utente
            Map<String, Object> params = new HashMap<>();
            params.put("userId", currentUserId);
            
            try (ClientConnection conn = new ClientConnection()) {
                Response r = conn.send("LISTA_LIBRERIE", params);
                if (r.ok) {
                    @SuppressWarnings("unchecked")
                    Map<String, String> librerie = (Map<String, String>) r.data.get("librerie");
                    
                    // Per ogni libreria, controlla se contiene questo libro
                    for (String libreriaIdStr : librerie.keySet()) {
                        int libreriaId = Integer.parseInt(libreriaIdStr);
                        Map<String, Object> libParams = new HashMap<>();
                        libParams.put("libreriaId", libreriaId);
                        
                        Response libR = conn.send("LIBRI_IN_LIBRERIA", libParams);
                        if (libR.ok) {
                            Type listType = new TypeToken<List<Libro>>(){}.getType();
                            List<Libro> libri = gson.fromJson(
                                gson.toJson(libR.data.get("libri")),
                                listType
                            );
                            
                            for (Libro l : libri) {
                                if (l.getId() == libro.getId()) {
                                    canRate = true;
                                    break;
                                }
                            }
                        }
                        
                        if (canRate) break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // Mostra o nascondi il TitledPane della valutazione
        if (valutazioneTitledPane != null) {
            valutazioneTitledPane.setVisible(canRate);
            valutazioneTitledPane.setManaged(canRate);
        }
    }

    private void loadStatsAndReviews(){
        if (libro == null) return;
        
        Map<String,Object> p = new HashMap<>();
        p.put("libroId", libro.getId());
        try (ClientConnection conn = new ClientConnection()){
            Response r = conn.send("BOOK_STATS_AND_REVIEWS", p);
            if (r.ok){
                Map<String, Object> data = r.data;
                Object media = data.getOrDefault("media", 0);
                Object count = data.getOrDefault("count", 0);
                
                double mediaVal = media instanceof Number ? ((Number)media).doubleValue() : 0.0;
                int countVal = count instanceof Number ? ((Number)count).intValue() : 0;
                
                mediaValue.setText(String.format("%.2f", mediaVal));
                numRecensioniValue.setText(String.valueOf(countVal));

                Type listType = new TypeToken<List<String>>(){}.getType();
                List<String> recs = gson.fromJson(gson.toJson(data.get("reviews")), listType);
                reviewsList.getItems().clear();
                reviewsList.getItems().setAll(recs);
            } else {
                mediaValue.setText("-");
                numRecensioniValue.setText("0");
                reviewsList.getItems().clear();
            }
        } catch (Exception e){
            e.printStackTrace();
            mediaValue.setText("-");
            numRecensioniValue.setText("0");
            reviewsList.getItems().clear();
        }
    }

    @FXML
    public void onSaveRating(){
        if (currentUserId <= 0) {
            showAlert("Errore", "Devi fare login prima");
            return;
        }

        // Validate selections
        if (comboStile.getValue() == null || comboContenuto.getValue() == null ||
            comboGradevolezza.getValue() == null || comboOriginalita.getValue() == null ||
            comboEdizione.getValue() == null) {
            showAlert("Errore", "Seleziona tutti i criteri di valutazione");
            return;
        }

        Map<String,Object> p = new HashMap<>();
        p.put("userId", currentUserId);
        p.put("libroId", libro.getId()); // Fixed: send as Integer, not String
        p.put("stile", comboStile.getValue());
        p.put("contenuto", comboContenuto.getValue());
        p.put("gradevolezza", comboGradevolezza.getValue());
        p.put("originalita", comboOriginalita.getValue());
        p.put("edizione", comboEdizione.getValue());
        p.put("note", noteValutazione.getText());

        try (ClientConnection conn = new ClientConnection()){
            Response r = conn.send("INSERISCI_VALUTAZIONE_LIBRO", p);
            if (r.ok) {
                showAlert("Successo", "Valutazione salvata");
                loadStatsAndReviews();
            } else {
                showAlert("Errore", r.message);
            }
        } catch (Exception e){
            e.printStackTrace();
            showAlert("Errore", "Errore di comunicazione: " + e.getMessage());
        }
    }

    @FXML
    public void onOpenRecommendationsDialog() {
        if (currentUserId <= 0) {
            showAlert("Errore", "Devi fare login prima");
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
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Errore", "Impossibile aprire dialog suggerimenti");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
}

