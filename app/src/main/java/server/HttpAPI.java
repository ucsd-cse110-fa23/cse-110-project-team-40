package server;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.json.JSONObject;

class RawHttpAPI implements HttpHandler {
  public String readQuery(URI uri, String base) {
    if (base.length() == uri.toString().length()) {
      /*handle no query case */
      return "";
    } else if (uri.toString().substring(base.length(), base.length() + 1).equals("/")) {
      /* handle subpage case */
      return uri.toString().substring(base.length() + 1);
    } else {
      /* handle regular query case */
      return uri.getQuery();
    }
  }

  public void handle(HttpExchange httpExchange) throws IOException {
    String response = "Request Received";
    String method = httpExchange.getRequestMethod();
    try {
      URI uri = httpExchange.getRequestURI();
      String base = httpExchange.getHttpContext().getPath();
      String query = readQuery(uri, base);
      System.err.println("QUERY WAS " + query);
      InputStream body = httpExchange.getRequestBody();
      if (method.equals("GET")) {
        response = handleGet(query, body);
      } else if (method.equals("POST")) {
        response = handlePost(query, body);
      } else if (method.equals("DELETE")) {
        response = handleDelete(query, body);
      } else if (method.equals("PUT")) {
        response = handlePut(query, body);
      } else {
        throw new Exception("Not Valid Request Method");
      }
    } catch (Exception e) {
      System.out.println("An erroneous request error: " + e.getMessage());
      response = "400 Bad Request";
      e.printStackTrace();
    }
    // Sending back response to the client
    sendResponse(httpExchange, response);
  }

  void sendResponse(HttpExchange httpExchange, String response) throws IOException {
    httpExchange.sendResponseHeaders(200, response.length());
    OutputStream outStream = httpExchange.getResponseBody();
    outStream.write(response.getBytes());
    outStream.close();
  }

  String getRequestString(InputStream body) throws IOException {
    Scanner scanner = new Scanner(body);
    /* Request body optional, so simply return null if not given */
    String postData;
    try {
      postData = scanner.nextLine();
    } catch (Exception e) {
      System.out.println("request was empty");
      scanner.close();
      return null;
    }
    System.out.println("request: " + postData);
    scanner.close();
    return postData;
  }

  String handleGet(String query, InputStream body) throws IOException {
    throw new IOException("Request type not supported");
  }

  String handlePost(String query, InputStream body) throws IOException {
    throw new IOException("Request type not supported");
  }

  String handlePut(String query, InputStream body) throws IOException {
    throw new IOException("Request type not supported");
  }

  String handleDelete(String query, InputStream body) throws IOException {
    throw new IOException("Request type not supported");
  }
}

class HttpAPI extends RawHttpAPI {

  JSONObject getJSONRequest(String request) throws IOException {
    try {
      return new JSONObject(request);
    } catch (Exception e) {
      throw new IOException("Response was not JSON");
    }
  }

  String handleGet(String query, InputStream body) throws IOException {
    String request = getRequestString(body);
    return handleGet(query, request);
  }

  String handlePost(String query, InputStream body) throws IOException {
    String request = getRequestString(body);
    return handlePost(query, request);
  }

  String handlePut(String query, InputStream body) throws IOException {
    String request = getRequestString(body);
    return handlePut(query, request);
  }

  String handleDelete(String query, InputStream body) throws IOException {
    String request = getRequestString(body);
    return handleDelete(query, request);
  }

  String handleGet(String query, String request) throws IOException {
    throw new IOException("Request type not supported");
  }

  String handlePost(String query, String request) throws IOException {
    throw new IOException("Request type not supported");
  }

  String handlePut(String query, String request) throws IOException {
    throw new IOException("Request type not supported");
  }

  String handleDelete(String query, String request) throws IOException {
    throw new IOException("Request type not supported");
  }
}
