package com.bookrecommender.client;

import com.bookrecommender.common.Request;
import com.bookrecommender.common.RequestType;
import com.bookrecommender.common.Response;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;

/**
 * Gestisce la connessione socket verso il serverBR.
 * Protocollo: una richiesta JSON per riga, una risposta JSON per riga.
 */
public class ClientConnection implements Closeable {

    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final Gson gson = new Gson();

    private static ClientConnection INSTANCE;

    public ClientConnection(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public static synchronized ClientConnection getInstance() {
        if (INSTANCE == null) {
            try {
                Properties p = new Properties();
                try (InputStream is = ClientConnection.class.getClassLoader().getResourceAsStream("app.properties")) {
                    if (is != null) p.load(is);
                }
                String host = p.getProperty("server.host", "127.0.0.1");
                int port = Integer.parseInt(p.getProperty("server.port", "5555"));
                INSTANCE = new ClientConnection(host, port);
            } catch (IOException e) {
                throw new RuntimeException("Impossibile connettersi al server: " + e.getMessage(), e);
            }
        }
        return INSTANCE;
    }

    public ClientConnection() throws IOException {
    Properties props = new Properties();
    try (InputStream in = ClientConnection.class
            .getClassLoader()
            .getResourceAsStream("app.properties")) {
        if (in != null) {
            props.load(in);
        }
    }

    // default nel caso manchino le proprietà
    String host = props.getProperty("server.host", "localhost");
    int port = Integer.parseInt(props.getProperty("server.port", "5555"));

    // riusa l’altro costruttore
    this.socket = new Socket(host, port);
    this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
}

    /** Invia una richiesta generica indicando il nome dell'operazione (RequestType) e un payload. */
    public Response send(String type, Map<String,Object> payload) {
        try {
            Request req = new Request();
            req.type = Enum.valueOf(RequestType.class, type);
            req.payload = payload;
            out.write(gson.toJson(req));
            out.write("\n");
            out.flush();
            String line = in.readLine();
            if (line == null) throw new IOException("Connessione chiusa dal server");
            return gson.fromJson(line, Response.class);
        } catch (IOException e){
            throw new RuntimeException("Errore di comunicazione col server: " + e.getMessage(), e);
        }
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
