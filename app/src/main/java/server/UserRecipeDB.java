package server;

import org.json.JSONObject;
import utils.Recipe;

public class UserRecipeDB implements RecipeData {
  private JSONDB db;
  private String user;

  UserRecipeDB(JSONDB db, String user) {
    this.db = db;
    this.user = user;
    clearFilters();
  }

  public void filterByMealType(String mealtype) {
    db.addFilter("mealtype", mealtype);
  }

  public void clearFilters() {
    db.clearFilters();
    db.addFilter("username", user);
  }

  public void encapsulate(JSONObject json) {
    json.put("username", this.user);
  }

  public void decapsulate(JSONObject json) {
    json.remove("username");
  }

  public Recipe put(String key, Recipe value) {
    System.out.println("Key " + key);
    JSONObject json = value.toJSON();
    encapsulate(json);
    db.remove(key);
    db.create(json);
    System.err.println("Created: " + json.toString());
    return get(key);
  }

  public Recipe get(String key) {
    JSONObject j = db.read(key);
    Recipe r;
    if (j == null) {
      return null;
    } else {
      decapsulate(j);
      return new Recipe(j);
    }
  }

  public Recipe remove(String key) {
    JSONObject j = db.remove(key);
    if (j == null) {
      return null;
    } else {
      decapsulate(j);
      return new Recipe(j);
    }
  }

  public JSONObject toJSON() {
    // TODO decapsulate each field
    return db.toJSON();
  }
}
