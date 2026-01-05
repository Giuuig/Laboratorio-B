
package com.bookrecommender.net;

public class Response {
    public boolean ok;
    public String message;
    public String json; // optional payload as JSON string

    public static Response ok(String json){ Response r = new Response(); r.ok=true; r.json=json; return r; }
    public static Response error(String m){ Response r = new Response(); r.ok=false; r.message=m; return r; }
}
