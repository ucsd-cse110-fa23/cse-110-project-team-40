/* Code initially adapted from Lab1 */

package PantryPal;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.event.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import org.json.JSONObject;
import utils.Account;
import utils.Recipe;

/** Communication model for making API requests to get Recipe List. */
public class RecipeListModel {
  HttpModel httpModel;
  Account account;

  RecipeListModel(HttpModel httpModel, Account account) {
    this.httpModel = httpModel;
    httpModel.setPath("recipes/" + account.getUsername() + "/");
  }

  public List<Recipe> getRecipeList() {
    String response = httpModel.performRequest("GET", null, null);
    try {
      return processResponse(response);
    } catch (Exception e) {
      System.err.println("HTTP request failed with error " + e.getMessage());
      return new ArrayList<Recipe>();
    }
  }

  public List<Recipe> getMealTypeRecipeList(String mealtype) {
    httpModel.performRequest("POST", mealtype, null);
    List<Recipe> recipeList = getRecipeList();
    httpModel.performRequest("DELETE", null, null);
    return recipeList;
  }

  /** convert JSON response into List of strings */
  public List<Recipe> processResponse(String response) throws IllegalArgumentException {
    try {
      JSONObject json = new JSONObject(response);
      List<Recipe> recipes = new ArrayList<Recipe>();
      Iterator<String> iRecipes = json.keys();
      while (iRecipes.hasNext()) {
        String title = iRecipes.next();
        recipes.add(new Recipe(json.getJSONObject(title)));
      }
      return recipes;
    } catch (Exception ex) {
      throw new IllegalArgumentException("response: \"" + response + "\" was invalid");
    }
  }
}
