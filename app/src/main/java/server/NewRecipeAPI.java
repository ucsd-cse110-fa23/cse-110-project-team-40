package server;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.Recipe;

interface InteractiveRecipeMaker extends Observer {
  /** get recipe if created, returns null otherwise */
  public Recipe getRecipe();

  /** Reset recipe maker to initial state */
  public void reset();

  /** gets interactive prompts to be displayed to user */
  public List<String> getPrompts();

  /** provide user-created prompt for processing */
  public void readResponse(String response);
}

class NewRecipeAPIFactory implements HttpUserAPIFactory {
  private RecipeCreator creator;
  private Map<String, WhisperSubject> perUserWhisperSubject;

  NewRecipeAPIFactory(RecipeCreator creator, Map<String, WhisperSubject> perUserWhisperSubject) {
    this.perUserWhisperSubject = perUserWhisperSubject;
    this.creator = creator;
  }

  public HttpAPI makeAPI(String username) {
    InteractiveRecipeMaker newRecipeCreator = new NewRecipeCreator(creator);
    WhisperSubject whisperSubject = perUserWhisperSubject.get(username);
    if (whisperSubject == null) {
      whisperSubject = new WhisperSubject();
      perUserWhisperSubject.put(username, whisperSubject);
    }
    System.err.println("NewRecipeFactory subject: " + whisperSubject.toString());
    whisperSubject.addObserver(newRecipeCreator);
    return new NewRecipeAPI(newRecipeCreator);
  }
}

class NewRecipeAPI extends HttpAPI {
  private InteractiveRecipeMaker creator;

  NewRecipeAPI(InteractiveRecipeMaker creator) {
    this.creator = creator;
  }

  public JSONObject makeResponseFromPrompts() {
    JSONObject json = new JSONObject();
    json.put("transcript", new JSONArray(creator.getPrompts()));

    /* if recipe is valid include it in response, then reset */
    if (creator.getRecipe() != null) {
      json.put("recipe", creator.getRecipe().toJSON());
      creator.reset();
    }
    return json;
  }

  /** Write responses */
  String handlePost(String query, String request) throws IOException {
    /* interpret request as JSON */
    JSONObject json = getJSONRequest(request);

    /* get client's prompt response from request's JSON */
    String requestResponse;
    try {
      requestResponse = json.getString("response");
    } catch (Exception e) {
      throw new IOException("Response was invalid");
    }
    /* interpret prompt response(ALLOW errors!) */
    try {
      creator.readResponse(requestResponse);
    } catch (Exception e) {
      System.err.println("response invalid with error " + e.getMessage());
    }

    /* responsd with prompts */
    String response = makeResponseFromPrompts().toString();

    return response;
  }

  /** Reset InteractiveRecipeMaker */
  String handleDelete(String query, String request) throws IOException {
    String response = "200 OK";
    creator.reset();
    return response;
  }

  /** get current prompts */
  String handleGet(String query, String request) throws IOException {
    String response = makeResponseFromPrompts().toString();
    return response;
  }
}
