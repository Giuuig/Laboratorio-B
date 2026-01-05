
package com.bookrecommender.client;

import com.bookrecommender.net.Request;
import com.bookrecommender.net.Response;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;

public class ClientConnection implements Closeable {
    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final Gson gson = new Gson();

    public ClientConnection() {
        try {
            Properties p = new Properties();
            p.load(ClientConnection.class.getClassLoader().getResourceAsStream("app.properties"));
            int port = Integer.parseInt(p.getProperty("server.port", "5555"));
            this.socket = new Socket("127.0.0.1", port);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Response send(String type, Map<String, String> payload){
        try {
            Request req = new Request();
            req.type = Enum.valueOf(com.bookrecommender.net.RequestType.class, type);
            req.payload = payload;
            out.write(gson.toJson(req));
            out.write("\n");
            out.flush();
            String line = in.readLine();
            return gson.fromJson(line, Response.class);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override public void close() throws IOException { socket.close(); }
}
