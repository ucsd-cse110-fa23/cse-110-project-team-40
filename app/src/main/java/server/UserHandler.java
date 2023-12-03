package server;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.util.*;

interface HttpUserAPIFactory {
  public RawHttpAPI makeAPI(String username);
}

class UserHandler extends RawHttpAPI {
  private HttpUserAPIFactory factory;
  private HashMap<String, RawHttpAPI> apis;

  UserHandler(HttpUserAPIFactory factory) {
    this.factory = factory;
    this.apis = new HashMap<>();
  }

  RawHttpAPI addAPI(String username) {
    if (!this.apis.containsKey(username)) {
      this.apis.put(username, factory.makeAPI(username));
    }
    return this.apis.get(username);
  }

  String[] makeFields(String query) throws IOException {
    String[] fields = query.split("/");
    if (fields.length == 1) {
      fields = new String[] {fields[0], ""};
    } else if (fields.length != 2) {
      throw new IOException("query did not follow format user/endpoint: " + query);
    }
    return fields;
  }

  String handlePost(String query, InputStream body) throws IOException {
    String[] fields = makeFields(query);
    String username = fields[0];
    String realQuery = fields[1];
    RawHttpAPI api = addAPI(username);
    return api.handlePost(realQuery, body);
  }

  String handlePut(String query, InputStream body) throws IOException {
    String[] fields = makeFields(query);
    String username = fields[0];
    String realQuery = fields[1];
    RawHttpAPI api = addAPI(username);
    return api.handlePut(realQuery, body);
  }

  String handleDelete(String query, InputStream body) throws IOException {
    String[] fields = makeFields(query);
    String username = fields[0];
    String realQuery = fields[1];
    RawHttpAPI api = addAPI(username);
    return api.handleDelete(realQuery, body);
  }

  String handleGet(String query, InputStream body) throws IOException {
    String[] fields = makeFields(query);
    String username = fields[0];
    String realQuery = fields[1];
    RawHttpAPI api = addAPI(username);
    return api.handleGet(realQuery, body);
  }
}
