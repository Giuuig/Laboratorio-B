
package com.bookrecommender.server;

import com.bookrecommender.net.*;
import com.bookrecommender.service.*;
import com.bookrecommender.model.Libro;
import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Properties;

public class ServerBR implements Runnable {
    private final int port;
    private volatile boolean running = true;
    private final Gson gson = new Gson();
    private final UserService userService = new UserService();
    private final BookService bookService = new BookService();
    private final RatingService ratingService = new RatingService();

    public ServerBR(int port){ this.port = port; }

    @Override public void run(){
        try (ServerSocket server = new ServerSocket(port)){
            while (running){
                Socket client = server.accept();
                new Thread(new SlaveThread(client)).start();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void stop(){ running = false; }

    private class SlaveThread implements Runnable {
        private final Socket socket;
        SlaveThread(Socket socket){ this.socket = socket; }

        @Override public void run(){
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))){
                String line;
                while ((line = in.readLine()) != null){
                    Request req = gson.fromJson(line, Request.class);
                    Response resp = handle(req);
                    out.write(gson.toJson(resp));
                    out.write("\n");
                    out.flush();
                }
            } catch (Exception e){
                // client closed
            }
        }

        private Response handle(Request r){
            try {
                switch (r.type){
                    case PING: return Response.ok("{\"pong\":true}");
                    case REGISTER: {
                        var u = userService.register(r.payload.get("nome"), r.payload.get("cognome"),
                                r.payload.get("email"), r.payload.get("password"));
                        return Response.ok("{\"userId\":" + u.getId() + "}");
                    }
                    case LOGIN: {
                        boolean ok = userService.login(r.payload.get("email"), r.payload.get("password"));
                        if (ok) return Response.ok("{\"login\":true}");
                        else return Response.error("Credenziali non valide");
                    }
                    case SEARCH_BOOKS: {
                        String titolo = r.payload.getOrDefault("titolo", "");
                        String autore = r.payload.getOrDefault("autore", "");
                        Integer anno = r.payload.get("anno") == null || r.payload.get("anno").isBlank() ? null : Integer.parseInt(r.payload.get("anno"));
                        List<Libro> libri = bookService.search(titolo, autore, anno);
                        return Response.ok(new Gson().toJson(libri));
                    }
                    case RATE_BOOK: {
                        int userId = Integer.parseInt(r.payload.get("userId"));
                        int libroId = Integer.parseInt(r.payload.get("libroId"));
                        int voto = Integer.parseInt(r.payload.get("voto"));
                        String commento = r.payload.getOrDefault("commento", "");
                        boolean ok = ratingService.rate(userId, libroId, voto, commento);
                        return ok ? Response.ok("{\"rated\":true}") : Response.error("Errore valutazione");
                    }
                    default: return Response.error("Richiesta non supportata");
                }
            } catch (Exception e){
                return Response.error("Errore: " + e.getMessage());
            }
        }
    }

    public static ServerBR startDefault(){
        try {
            Properties p = new Properties();
            p.load(ServerBR.class.getClassLoader().getResourceAsStream("app.properties"));
            int port = Integer.parseInt(p.getProperty("server.port", "5555"));
            ServerBR s = new ServerBR(port);
            Thread t = new Thread(s, "ServerBR-Main");
            t.setDaemon(true);
            t.start();
            return s;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
