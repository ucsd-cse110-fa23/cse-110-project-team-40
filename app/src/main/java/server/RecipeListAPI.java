package server;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.util.*;

class RecipeListAPIFactory implements HttpUserAPIFactory {
  public HttpAPI makeAPI(String username) {
    return new RecipeListAPI(new UserRecipeDB(new MongoJSONDB("recipes", "title"), username));
  }
}

class RecipeListAPI extends HttpAPI {
  private RecipeData data;

  RecipeListAPI(RecipeData map) {
    this.data = map;
  }

  String handleGet(String query, String request) throws IOException {
    return data.toJSON().toString();
  }

  String handlePost(String query, String request) throws IOException {
    String result = "200 OK";
    if (query.equals("breakfast")) {
      data.filterByMealType("breakfast");
    } else if (query.equals("lunch")) {
      data.filterByMealType("lunch");
    } else if (query.equals("dinner")) {
      data.filterByMealType("dinner");
    } else {
      result = "400 Bad Request";
    }
    return result;
  }

  String handleDelete(String query, String request) throws IOException {
    data.clearFilters();
    return "200 OK";
  }
}
