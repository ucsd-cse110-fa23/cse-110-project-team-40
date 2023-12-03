package server;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.HashMap;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Recipe;

class MockJSONDB implements JSONDB {
  public HashMap<String, JSONObject> data;
  public HashMap<String, String> filters;
  public JSONObject representation;
  public String key;

  MockJSONDB(String key) {
    this.key = key;
    this.data = new HashMap<>();
    this.filters = new HashMap<>();
  }

  public JSONObject remove(JSONObject json) {
    return remove(json.getString(key));
  }

  public JSONObject remove(String key) {
    return data.remove(key);
  }

  public void create(JSONObject json) {
    data.put(json.getString(key), json);
  }

  public void update(JSONObject json) {
    create(json);
  }

  public JSONObject read(JSONObject json) {
    return read(json.getString(key));
  }

  public JSONObject read(String key) {
    /* create a copy then give copy */
    return new JSONObject(data.get(key).toString());
  }

  public void addFilter(String key, String val) {
    filters.put(key, val);
  }

  public void clearFilters() {
    filters.clear();
  }

  public void clear() {
    data.clear();
  }

  public JSONObject toJSON() {
    return representation;
  }
}

public class UserRecipeDBTest {
  UserRecipeDB recipeDB;
  MockJSONDB mockJSONDB;
  String recipeTitle = "corn";
  Recipe recipe = new Recipe(recipeTitle, "dinner", "boil corn");
  JSONObject recipeJSON;
  JSONObject encapsulatedRecipeJSON;
  String username;

  @BeforeEach
  public void setup() {
    username = "john";
    recipeJSON = recipe.toJSON();
    encapsulatedRecipeJSON = recipe.toJSON();
    encapsulatedRecipeJSON.put("username", username);
    mockJSONDB = new MockJSONDB("title");
    recipeDB = new UserRecipeDB(mockJSONDB, username);
  }

  @Test
  public void testEncapsulate() {
    recipeDB.encapsulate(recipeJSON);
    assertEquals(recipeJSON.toString(), encapsulatedRecipeJSON.toString());
  }

  @Test
  public void testDecapsulate() {
    recipeDB.decapsulate(encapsulatedRecipeJSON);
    assertEquals(encapsulatedRecipeJSON.toString(), recipeJSON.toString());
  }

  @Test
  public void testPut() {
    assertEquals(recipe.toJSON().toString(), recipeDB.put(recipeTitle, recipe).toJSON().toString());
    assertEquals(
        true,
        encapsulatedRecipeJSON.similar(mockJSONDB.data.get(recipeTitle)),
        mockJSONDB.data.get(recipeTitle).toString());
  }

  @Test
  public void testGet() {
    mockJSONDB.data.put(recipeTitle, encapsulatedRecipeJSON);
    assertEquals(recipe.toJSON().toString(), recipeDB.get(recipeTitle).toJSON().toString());
  }

  @Test
  public void testRemove() {
    mockJSONDB.data.put(recipeTitle, encapsulatedRecipeJSON);
    assertEquals(recipe, recipeDB.remove(recipeTitle));
    assertEquals(false, mockJSONDB.data.containsKey(recipeTitle));
  }

  @Test
  public void testClearFilters() {
    recipeDB.filterByMealType("dinner");
    recipeDB.clearFilters();
    assertEquals(username, mockJSONDB.filters.get("username"));
    assertEquals(1, mockJSONDB.filters.size());
  }

  @Test
  public void testFilterByMealType() {
    recipeDB.filterByMealType("dinner");
    assertEquals("dinner", mockJSONDB.filters.get("mealtype"));
  }
}
