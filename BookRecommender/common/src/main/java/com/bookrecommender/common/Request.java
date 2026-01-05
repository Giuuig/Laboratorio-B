package com.bookrecommender.common;

import java.util.Map;

/** Richiesta generica verso il server, serializzata in JSON. */
public class Request {
    public RequestType type;
    public Map<String, Object> payload;

    public Request() {}

    public Request(RequestType type, Map<String,Object> payload){
        this.type = type;
        this.payload = payload;
    }
}
