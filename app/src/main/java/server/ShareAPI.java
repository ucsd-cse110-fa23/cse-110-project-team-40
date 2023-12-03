package server;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.util.*;
import utils.Recipe;

interface RecipePageMaker {
  public String makePage(Recipe r);
}

class ShareRecipePageMaker implements RecipePageMaker {
  private ImageManager manager;

  ShareRecipePageMaker(ImageManager manager) {
    this.manager = manager;
  }

  public String makeImage(String base64Image) {
    return "<p> <b> <img src='data:image/jpeg;base64," + base64Image + "'/> </b></p>\n";
  }

  public String makeRecipeDescription(Recipe r) {
    String content = "";
    content += "<h1> " + r.getTitle() + "</h1>\n";
    content += "<p> <b> Meal Type: </b> " + r.getMealType() + "</p>\n";
    content += "<p> <b> Description: </b> " + r.getDescription() + "</p>\n";
    return content;
  }

  public String makePage(Recipe r) {
    String content = "<html>\n";
    content += makeRecipeDescription(r);
    try {
      content += makeImage(manager.getImageAsBase64(Recipe.sanitizeTitle(r.getTitle())));
    } catch (Exception e) {
      System.err.println("failed to add image to share page");
    }
    content += "<html>";
    return content;
  }
}

class ShareAPIFactory implements HttpUserAPIFactory {
  Map<String, ImageManager> perUserImageManager;

  ShareAPIFactory(Map<String, ImageManager> perUserImageManager) {
    this.perUserImageManager = perUserImageManager;
  }

  public HttpAPI makeAPI(String username) {
    ImageManager manager = perUserImageManager.get(username);
    if (manager == null) {
      manager = new ImageManager(new DalleBot(username));
      perUserImageManager.put(username, manager);
    }
    return new ShareAPI(
        new UserRecipeDB(new JSONDB("recipes", "title"), username),
        new ShareRecipePageMaker(manager));
  }
}

/** API which creates Http pages for sharing */
class ShareAPI extends HttpAPI {
  private RecipeData data;
  private RecipePageMaker recipePageMaker;

  ShareAPI(RecipeData map, RecipePageMaker recipePageMaker) {
    this.data = map;
    this.recipePageMaker = recipePageMaker;
  }

  /** Get a specific recipe */
  String handleGet(String query, String request) throws IOException {
    String response = "404 Not Found";
    query = query.substring(query.indexOf("?") + 1);
    if (query != null) {
      Recipe recipe = data.get(Recipe.desanitizeTitle(query)); // get recipe
      if (recipe != null) {
        response = recipePageMaker.makePage(recipe);
      }
    }
    return response;
  }
}
