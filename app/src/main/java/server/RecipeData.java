package server;

import org.json.JSONObject;
import utils.Recipe;

/** Interface of object which stores data for Recipes */
public interface RecipeData {
  public JSONObject toJSON();

  public Recipe put(String key, Recipe value);

  public Recipe get(String key);

  public Recipe remove(String key);

  public void filterByMealType(String mealtype);

  public void clearFilters();
}
