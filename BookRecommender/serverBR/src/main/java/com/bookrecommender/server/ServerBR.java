package com.bookrecommender.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.bookrecommender.common.Request;
import com.bookrecommender.common.RequestType;
import com.bookrecommender.common.Response;
import com.bookrecommender.server.dao.BookDAO;
import com.bookrecommender.server.dao.DBManager;
import com.bookrecommender.server.dao.LibraryDAO;
import com.bookrecommender.server.dao.RatingDAO;
import com.bookrecommender.server.dao.SuggestionDAO;
import com.bookrecommender.server.dao.UserDAO;
import com.bookrecommender.server.model.Libro;
import com.google.gson.Gson;

/**
 * Server concorrente BookRecommender.
 * Usa protocollo JSON riga-per-riga con Request/Response definiti nel modulo common.S
 */
public class ServerBR {

    private final int port;
    private final Gson gson = new Gson();
    private final ExecutorService pool = Executors.newCachedThreadPool();

    private final BookDAO bookDAO = new BookDAO();
    private final UserDAO userDAO = new UserDAO();
    private final LibraryDAO libraryDAO = new LibraryDAO();
    private final RatingDAO ratingDAO = new RatingDAO();
    private final SuggestionDAO suggestionDAO = new SuggestionDAO();

    public ServerBR(int port){
        this.port = port;
    }

    public void start() throws Exception {
        DBManager.init();
        try (ServerSocket ss = new ServerSocket(port)){
            System.out.println("ServerBR in ascolto sulla porta " + port);
            while (true){
                Socket s = ss.accept();
                pool.submit(() -> handleClient(s));
            }
        }
    }

    private void handleClient(Socket s){
        try (BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))){
            String line;
            while ((line = in.readLine()) != null){
                Response resp;
                try {
                    Request req = gson.fromJson(line, Request.class);
                    resp = dispatch(req);
                } catch (Exception e){
                    e.printStackTrace();
                    resp = Response.error("Errore lato server: " + e.getMessage());
                }
                out.write(gson.toJson(resp));
                out.write("\n");
                out.flush();
            }
        } catch (IOException ignored){
        }
    }

    private Response dispatch(Request req) throws Exception {
        Map<String,Object> p = req.payload == null ? new HashMap<>() : req.payload;
        Map<String,Object> data = new HashMap<>();

        RequestType type = req.type;
        if (type == null) return Response.error("RequestType mancante");

        switch (type){
            case PING: {
                data.put("pong", true);
                return Response.ok(data);
            }
            // LOGIN & REGISTER (nomi GUI)
            case LOGIN: {
                String userid = (String)p.get("userid");
                // Fallback a email se userid non fornito (compatibilità)
                if (userid == null || userid.isEmpty()) {
                    userid = (String)p.get("email");
                }
                String password = (String)p.get("password");
                Integer id = userDAO.login(userid, password);
                if (id == null) return Response.error("Credenziali non valide");
                String nome = userDAO.getNomeById(id);
                data.put("userId", id);
                data.put("nome", nome);
                return Response.ok(data);
            }
            case REGISTER: {
                String nome   = (String) p.get("nome");
                String cognome= (String) p.get("cognome");
                String codiceFiscale = (String) p.get("codiceFiscale");
                String email  = (String) p.get("email");
                String userid = (String) p.get("userid");
                String pwd    = (String) p.get("password");
                
                // Validazione campi obbligatori
                if (nome == null || nome.isEmpty() || cognome == null || cognome.isEmpty() ||
                    codiceFiscale == null || codiceFiscale.isEmpty() ||
                    email == null || email.isEmpty() || userid == null || userid.isEmpty() ||
                    pwd == null || pwd.isEmpty()) {
                    return Response.error("Tutti i campi sono obbligatori");
                }
                
                // Validazione lunghezza codice fiscale
                if (codiceFiscale.length() != 16) {
                    return Response.error("Il codice fiscale deve essere di 16 caratteri");
                }
                
                userDAO.register(nome, cognome, codiceFiscale, email, userid, pwd);
                data.put("email", email);
                data.put("userid", userid);
                return Response.ok(data);
            }
            // SEARCH_BOOKS usato dalla GUI: ricerca semplice per titolo (substring, case-insensitive)
            case SEARCH_BOOKS: {
                String q = (String)p.getOrDefault("q", "");
                List<Libro> libri = bookDAO.searchByTitle(q);
                data.put("libri", libri);
                return Response.ok(data);
            }
            // RATE_BOOK usato dalla GUI: 1 voto globale + commento
            case RATE_BOOK: {
                int userId = ((Number)p.get("userId")).intValue();
                int libroId = Integer.parseInt((String)p.get("libroId"));
                int voto = Integer.parseInt((String)p.get("voto"));
                String commento = (String)p.get("commento");

                // Verifica che l'utente sia registrato (userId > 0)
                if (userId <= 0) {
                    return Response.error("Devi essere registrato per inserire una recensione");
                }

                // Verifica che il libro sia presente in una libreria dell'utente
                if (!libraryDAO.libroNelleMieLibrerie(userId, libroId)) {
                    return Response.error("Puoi recensire solo libri presenti nelle tue librerie");
                }

                // Mappo il voto globale sui 5 criteri
                ratingDAO.inserisci(userId, libroId, voto, voto, voto, voto, voto, commento);
                return Response.ok(data);
            }


            case BOOK_STATS_AND_REVIEWS: {
                int libroId = ((Number)p.get("libroId")).intValue();

                // Uso gli aggregati già disponibili per il voto finale
                Map<String,Object> agg = ratingDAO.aggregati(libroId);
                @SuppressWarnings("unchecked")
                Map<String,Object> votoFinale = (Map<String,Object>) agg.get("voto_finale");

                double media = 0.0;
                int count = 0;
                if (votoFinale != null){
                    Object mediaObj = votoFinale.get("media");
                    if (mediaObj != null) media = ((Number)mediaObj).doubleValue();
                    for (int i = 1; i <= 5; i++){
                        Object c = votoFinale.get("c" + i);
                        if (c != null) count += ((Number)c).intValue();
                    }
                }

                // Recupera recensioni con utente_id per poter aggiungere i suggerimenti
                java.util.List<Map<String,Object>> reviewsWithUsers = ratingDAO.reviewsWithUsers(libroId);
                java.util.List<String> reviewsOut = new java.util.ArrayList<>();
                
                for (Map<String,Object> rm : reviewsWithUsers) {
                    int uid = ((Number)rm.get("utenteId")).intValue();
                    double votoFinaleSingolo = ((Number)rm.get("votoFinale")).doubleValue();
                    String note = (String) rm.get("note");
                    
                    StringBuilder sb = new StringBuilder();
                    sb.append(String.format("[Voto: %.2f] ", votoFinaleSingolo));
                    
                    if (note != null && !note.trim().isEmpty()) {
                        sb.append(note);
                    } else {
                        sb.append("(Nessuna nota)");
                    }
                    
                    // Recupera i titoli dei libri suggeriti da questo utente per questo libro
                    List<String> suggeriti = suggestionDAO.getTitoliSuggeritiPerUtenteLibro(uid, libroId);
                    
                    if (!suggeriti.isEmpty()) {
                        sb.append("\n\nLibri consigliati: ");
                        sb.append(String.join(", ", suggeriti));
                    }
                    
                    reviewsOut.add(sb.toString());
                }
                
                data.put("media", media);
                data.put("count", count);
                data.put("reviews", reviewsOut);
                return Response.ok(data);
            }

            // SEARCH_BOOKS_ADVANCED: ricerca avanzata per titolo, autore, anno
            case SEARCH_BOOKS_ADVANCED: {
                String titolo = (String)p.getOrDefault("titolo", "");
                String autore = (String)p.getOrDefault("autore", "");
                String annoStr = (String)p.getOrDefault("anno", "");
                
                List<Libro> libri;
                if (!titolo.isEmpty()) {
                    libri = bookDAO.searchByTitle(titolo);
                } else if (!autore.isEmpty() && !annoStr.isEmpty()) {
                    try {
                        int anno = Integer.parseInt(annoStr);
                        libri = bookDAO.searchByAuthorYear(autore, anno);
                    } catch (NumberFormatException e) {
                        libri = bookDAO.searchByAuthor(autore);
                    }
                } else if (!autore.isEmpty()) {
                    libri = bookDAO.searchByAuthor(autore);
                } else {
                    // Nessun criterio: restituisci tutti i libri
                    libri = bookDAO.searchByTitle("");
                }
                data.put("libri", libri);
                return Response.ok(data);
            }

            // Operazioni con nomi delle specifiche: wrapper verso le stesse logiche
            case CERCA_TITOLO: {
                String q = (String)p.getOrDefault("q", "");
                List<Libro> libri = bookDAO.searchByTitle(q);
                data.put("libri", libri);
                return Response.ok(data);
            }
            case CERCA_AUTORE: {
                String q = (String)p.getOrDefault("autore", "");
                List<Libro> libri = bookDAO.searchByAuthor(q);
                data.put("libri", libri);
                return Response.ok(data);
            }
            case CERCA_AUTORE_ANNO: {
                String autore = (String)p.getOrDefault("autore", "");
                int anno = ((Number)p.get("anno")).intValue();
                List<Libro> libri = bookDAO.searchByAuthorYear(autore, anno);
                data.put("libri", libri);
                return Response.ok(data);
            }
            case VISUALIZZA_LIBRO: {
                int libroId = ((Number)p.get("libroId")).intValue();
                Libro l = bookDAO.findById(libroId);
                Map<String,Object> aggregati = ratingDAO.aggregati(libroId);
                var consigli = suggestionDAO.conteggioPerLibro(libroId);
                data.put("libro", l);
                data.put("aggregati", aggregati);
                data.put("consigli", consigli);
                return Response.ok(data);
            }
            case REGISTRAZIONE: {
                // alias di REGISTER
                String nome   = (String) p.get("nome");
                String cognome= (String) p.get("cognome");
                String codiceFiscale = (String) p.get("codiceFiscale");
                String email  = (String) p.get("email");
                String userid = (String) p.get("userid");
                String pwd    = (String) p.get("password");
                
                // Validazione campi obbligatori
                if (nome == null || nome.isEmpty() || cognome == null || cognome.isEmpty() ||
                    codiceFiscale == null || codiceFiscale.isEmpty() ||
                    email == null || email.isEmpty() || userid == null || userid.isEmpty() ||
                    pwd == null || pwd.isEmpty()) {
                    return Response.error("Tutti i campi sono obbligatori");
                }
                
                // Validazione lunghezza codice fiscale
                if (codiceFiscale.length() != 16) {
                    return Response.error("Il codice fiscale deve essere di 16 caratteri");
                }

                int id = userDAO.register(nome, cognome, codiceFiscale, email, userid, pwd);
                data.put("email", email);
                data.put("userid", userid);
                return Response.ok(data);
            }
            case REGISTRA_LIBRERIA: {
                int utenteId = ((Number)p.get("userId")).intValue();
                String nome = (String)p.get("nome");
                int id = libraryDAO.creaLibreria(utenteId, nome);
                data.put("libreriaId", id);
                return Response.ok(data);
            }
            case AGGIUNGI_LIBRI_A_LIBRERIA: {
                int libreriaId = ((Number)p.get("libreriaId")).intValue();
                @SuppressWarnings("unchecked")
                List<Double> libriD = (List<Double>) p.get("libri");
                List<Integer> ids = new ArrayList<>();
                for (Double d : libriD) ids.add(d.intValue());
                libraryDAO.aggiungiLibri(libreriaId, ids);
                return Response.ok(data);
            }
            case LISTA_LIBRERIE: {
                int utenteId = ((Number)p.get("userId")).intValue();
                var librerieMap = libraryDAO.listaConId(utenteId);
                data.put("librerie", librerieMap);
                return Response.ok(data);
            }
            case LIBRI_IN_LIBRERIA: {
                int libreriaId = ((Number)p.get("libreriaId")).intValue();
                var libroIds = libraryDAO.libriInLibreria(libreriaId);
                var libri = new ArrayList<Libro>();
                for (int id : libroIds) {
                    var libro = bookDAO.findById(id);
                    if (libro != null) libri.add(libro);
                }
                data.put("libri", libri);
                return Response.ok(data);
            }
            case INSERISCI_VALUTAZIONE_LIBRO: {
                int utenteId = ((Number)p.get("userId")).intValue();
                int libroId = ((Number)p.get("libroId")).intValue();
                
                // Verifica che l'utente sia registrato
                if (utenteId <= 0) {
                    return Response.error("Devi essere registrato per inserire una recensione");
                }
                
                // Verifica che il libro sia nelle librerie dell'utente
                if (!libraryDAO.libroNelleMieLibrerie(utenteId, libroId)) {
                    return Response.error("Puoi recensire solo libri presenti nelle tue librerie");
                }
                
                int stile = ((Number)p.get("stile")).intValue();
                int contenuto = ((Number)p.get("contenuto")).intValue();
                int gradevolezza = ((Number)p.get("gradevolezza")).intValue();
                int originalita = ((Number)p.get("originalita")).intValue();
                int edizione = ((Number)p.get("edizione")).intValue();
                String note = (String)p.get("note");
                ratingDAO.inserisci(utenteId, libroId, stile, contenuto, gradevolezza, originalita, edizione, note);
                return Response.ok(data);
            }
            case INSERISCI_SUGGERIMENTO_LIBRO: {
                int utenteId = ((Number)p.get("userId")).intValue();
                int libroId = ((Number)p.get("libroId")).intValue();
                @SuppressWarnings("unchecked")
                List<Double> suggD = (List<Double>) p.get("suggeriti");
                List<Integer> sugg = new ArrayList<>();
                for (Double d : suggD) sugg.add(d.intValue());
                suggestionDAO.inserisci(utenteId, libroId, sugg);
                return Response.ok(data);
            }
            default: {
                return Response.error("Operazione non supportata: " + type);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 5555;
        if (args.length > 0) port = Integer.parseInt(args[0]);
        new ServerBR(port).start();
    }
}
