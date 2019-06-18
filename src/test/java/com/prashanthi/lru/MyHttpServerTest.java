package com.prashanthi.lru;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MyHttpServerTest {

class Response {
    int responseCode;
    String response;
}

private MyHttpServer server;

private Response sendGetValid(int key) throws IOException {
    String getUrl = "http://localhost:8080/api/v1/get/" + key;
    return sendGet(getUrl);
}

private Response sendGet(String getUrl) throws IOException{
    Response rsp = new Response();

    URL obj = new URL(getUrl);
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
    con.setRequestMethod("GET");
    int responseCode = con.getResponseCode();
    System.out.println("GET Response Code :: " + responseCode);
    rsp.responseCode = responseCode;
    if (responseCode == HttpURLConnection.HTTP_OK) { // success
        BufferedReader in = new BufferedReader(new InputStreamReader(
                                con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        // print result
        System.out.println(response.toString());
        rsp.response = response.toString();
    } else {
        System.out.println("GET request not working");
        rsp.response = "";
    }
    return rsp;
}

private Response sendPutValid(int key, int value) throws IOException {
    String postUrl = "http://localhost:8080/api/v1/put/" + key;
    String postBody = "value=" + value;

    return sendPut(postUrl, postBody);
}

private Response sendPut(String postUrl, String postBody) throws IOException {
    Response rsp = new Response();

    URL obj = new URL(postUrl);
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
    con.setRequestMethod("POST");

    con.setDoOutput(true);
    OutputStream os = con.getOutputStream();
    os.write(postBody.getBytes());
    os.flush();
    os.close();

    int responseCode = con.getResponseCode();
    System.out.println("POST Response Code :: " + responseCode);
    rsp.responseCode = responseCode;
    if (responseCode == HttpURLConnection.HTTP_OK) { //success
        BufferedReader in = new BufferedReader(new InputStreamReader(
            con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // print result
        System.out.println(response.toString());
        rsp.response = response.toString();
    } else {
        System.out.println("POST request not working");
        rsp.response = "";
    }
    return rsp;
}

@Before
public void setUp() throws Exception {
    server = new MyHttpServer("127.0.0.1", 8080, 2);
    server.start();
}

@After
public void tearDown() throws Exception {
    server.stop();
}

@Test
public void testGetEntryNotPresent() throws IOException {
    Response rsp = sendGetValid(4);
    assertEquals(rsp.responseCode, 404);
}

@Test
public void testGetInvalidKey() throws IOException {
    Response rsp = sendGet("http://localhost:8080/api/v1/get/abc");
    assertEquals(rsp.responseCode, 404);
}

@Test
public void testPutInvalidKey() throws IOException {
    Response rsp = sendPut("http://localhost:8080/api/v1/put/9", "value=abc");
    assertEquals(rsp.responseCode, 404);
}

@Test
public void testPutInvalidValue() throws IOException {
    Response rsp = sendPut("http://localhost:8080/api/v1/put/abc", "value=1500");
    assertEquals(rsp.responseCode, 404);
}

@Test
public void testPutWithEviction() throws IOException {

    Response rsp = sendPutValid(8, 800);
    assertEquals(rsp.responseCode, 200);

    rsp = sendGetValid(8);
    assertEquals(rsp.responseCode, 200);
    assertEquals(rsp.response, "{ key: 8, value: 800 }");

    rsp = sendPutValid(15, 1500);
    assertEquals(rsp.responseCode, 200);

    rsp = sendPutValid(1, 100);
    assertEquals(rsp.responseCode, 200);
    assertEquals(rsp.response, "{ key: 8, value: 800 }");

    rsp =sendPutValid(2, 200);
    assertEquals(rsp.responseCode, 200);
    assertEquals(rsp.response, "{ key: 15, value: 1500 }");

    rsp = sendGetValid(1);
    assertEquals(rsp.responseCode, 200);
    assertEquals(rsp.response, "{ key: 1, value: 100 }");

    rsp = sendPutValid(3, 300);
    assertEquals(rsp.responseCode, 200);
    assertEquals(rsp.response, "{ key: 2, value: 200 }");

    rsp = sendPutValid(4, 400);
    assertEquals(rsp.responseCode, 200);
    assertEquals(rsp.response, "{ key: 1, value: 100 }");

    rsp = sendPutValid(3, 1200);
    assertEquals(rsp.responseCode, 200);

    rsp = sendGetValid(3);
    assertEquals(rsp.responseCode, 200);
    assertEquals(rsp.response, "{ key: 3, value: 1200 }");
}
}
