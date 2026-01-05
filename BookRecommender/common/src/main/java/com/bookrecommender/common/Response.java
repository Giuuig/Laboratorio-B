package com.bookrecommender.common;

import java.util.Map;

/** Risposta generica del server. */
public class Response {
    public boolean ok;
    public String message;
    public Map<String,Object> data;

    public Response() {}

    public Response(boolean ok, String message, Map<String,Object> data){
        this.ok = ok;
        this.message = message;
        this.data = data;
    }

    public static Response ok(Map<String,Object> data){
        return new Response(true, null, data);
    }

    public static Response error(String message){
        return new Response(false, message, null);
    }
}
