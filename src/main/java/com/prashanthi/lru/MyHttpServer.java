package com.prashanthi.lru;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


class MyGetHandler implements HttpHandler {

private LruHash lru;

public MyGetHandler(LruHash lru) {
    this.lru = lru;
}

public void handle(HttpExchange exchange) throws IOException {
    System.out.println("Recd a GET request");

    String requestMethod = exchange.getRequestMethod();
    if (requestMethod.equalsIgnoreCase("GET")) {
        String delimiters = "\\/";
        String[] tokens = exchange.getRequestURI().getPath().split(delimiters);
        if (tokens.length != 5) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        int key = 0;
        try {
            key = Integer.parseInt(tokens[4]);
        } catch(NumberFormatException e) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        int result[] = lru.get(key);
        if (result == null) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        String output = "{ ";
        output += "key: " + result[0] + ", ";
        output += "value: " + result[1];
        output += " }";
        exchange.sendResponseHeaders(200, output.length());

        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(output.getBytes());
        responseBody.close();

        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set("Content-Type", "text/plain");
    }
}

} // End class MyGetHandler

class MyPutHandler implements HttpHandler {

private LruHash lru;

public MyPutHandler(LruHash lru) {
    this.lru = lru;
}

public void handle(HttpExchange exchange) throws IOException {
    System.out.println("Recd a PUT request");
    String requestMethod = exchange.getRequestMethod();
    if (requestMethod.equalsIgnoreCase("POST")) {
        String delimiters = "\\/";
        String[] tokens = exchange.getRequestURI().getPath().split(delimiters);
        if (tokens.length != 5) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        int key = 0;
        try {
            key = Integer.parseInt(tokens[4]);
        } catch(NumberFormatException e) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        InputStreamReader s = new InputStreamReader(exchange.getRequestBody());
        BufferedReader br = new BufferedReader(s);
        String query = br.readLine();
        int value=0;
        if (query != null) {
            delimiters = "\\=";
            tokens = query.split(delimiters) ;
            try {
                value = Integer.parseInt(tokens[1]);
            } catch (NumberFormatException e) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }
        }

        // Valid key and value. Lets go...
        int[] result = lru.put(key, value);
        if (result != null) {
            String output = "{ ";
            output += "key: " + result[0] + ", ";
            output += "value: " + result[1];
            output += " }";
            exchange.sendResponseHeaders(200, output.length());

            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(output.getBytes());
            responseBody.close();

            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.set("Content-Type", "text/plain");
            return;
        }
    }

    Headers responseHeaders = exchange.getResponseHeaders();
    responseHeaders.set("Content-Type", "text/plain");
    exchange.sendResponseHeaders(200, 0);

    OutputStream responseBody = exchange.getResponseBody();
    responseBody.close();
}

} // End class MyPutHandler

public class MyHttpServer {

private HttpServer server;

public MyHttpServer(String hostname, int port, int capacity) throws Exception {
    LruHash lru = new LruHash(capacity);

    InetSocketAddress addr = new InetSocketAddress(hostname, port);
    server = HttpServer.create(addr, 0);
    server.createContext("/api/v1/put", new MyPutHandler(lru));
    server.createContext("/api/v1/get", new MyGetHandler(lru));
    server.setExecutor(Executors.newCachedThreadPool());
}

public void start() {
    server.start();
}

public void stop() {
    server.stop(0);
}

public static void main(String args[]) throws Exception {
    int capacity = 2;
    try {
        capacity = Integer.parseInt(args[0]);
    } catch(Exception e) {
    }

    System.out.printf("Starting HTTP server on port 8080 with capacity %d\n", capacity);
    MyHttpServer server = new MyHttpServer("127.0.0.1", 8080, capacity);
    server.start();
}

} // end class MyHttpServer

