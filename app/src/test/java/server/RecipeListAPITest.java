package server;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.HashMap;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import utils.Recipe;

class MockRecipeData implements RecipeData {
  public JSONObject representation = new JSONObject();
  public HashMap<String, Recipe> data = new HashMap<>();
  String filter = "";

  public JSONObject toJSON() {
    return representation;
  }

  public Recipe put(String key, Recipe value) {
    return data.put(key, value);
  }

  public Recipe get(String key) {
    return data.get(key);
  }

  public Recipe remove(String key) {
    return data.remove(key);
  }

  public void filterByMealType(String mealtype) {
    this.filter = mealtype;
  }

  public void clearFilters() {
    this.filter = "";
  }
}

public class RecipeListAPITest {
  MockRecipeData data = new MockRecipeData();
  RecipeListAPI dut = new RecipeListAPI(data);
  String exceptionMessage = "Request type not supported";

  @Test
  public void testDelete() {
    data.filter = "NonEmptyFilter";
    try {
      assertEquals("200 OK", dut.handleDelete("", ""));
    } catch (Exception e) {
      fail("delete should not throw exception");
    }
    assertEquals("", data.filter);
  }

  @Test
  public void testPut() {
    boolean exceptionHappened = false;
    try {
      dut.handlePut("", "");
    } catch (IOException e) {
      exceptionHappened = true;
      assertEquals(exceptionMessage, e.getMessage());
    }
    assertEquals(true, exceptionHappened);
  }

  @Test
  public void testPost() {
    try {
      assertEquals("200 OK", dut.handlePost("breakfast", ""));
      assertEquals("breakfast", data.filter);
      assertEquals("200 OK", dut.handlePost("lunch", ""));
      assertEquals("lunch", data.filter);
      assertEquals("200 OK", dut.handlePost("dinner", ""));
      assertEquals("dinner", data.filter);
      assertEquals("400 Bad Request", dut.handlePost("thanksgiving", ""));
    } catch (IOException e) {
      fail("post should not throw exception");
    }
  }

  @Test
  public void testGet() {
    try {
      // Setup data
      MockRecipeData data = new MockRecipeData();
      MockRecipeData data2 = new MockRecipeData();
      RecipeListAPI api = new RecipeListAPI(data);
      RecipeListAPI api2 = new RecipeListAPI(data2);
      data.put("Scrambled eggs", new Recipe("Scrambled eggs", "breakfast", "eggs"));
      data.put(
          "Mac and cheese", new Recipe("Mac and cheese", "dinner", "step 1. mac\nstep 2.cheese"));
      data.representation.put("Scrambled eggs", data.get("Scrambled eggs"));
      data.representation.put("Mac and cheese", data.get("Mac and cheese"));

      /* test empty data */
      assertEquals(data.toJSON().toString(), api.handleGet("", ""), "get failed for empty data");
      assertEquals(
          data2.toJSON().toString(),
          api2.handleGet("", ""),
          "get failed for empty data,empty list");

      /* test null data */
      assertEquals(
          data.toJSON().toString(), api.handleGet(null, (String) null), "get failed for null data");
      assertEquals(
          data2.toJSON().toString(),
          api2.handleGet(null, (String) null),
          "get failed for null data, empty list");

      /* test valid data */
      assertEquals(
          data.toJSON().toString(), api.handleGet("hi", "hello"), "get failed for valid data");
      assertEquals(
          data2.toJSON().toString(),
          api2.handleGet("hi", "hello"),
          "get failed for valid data, empty list");
    } catch (Exception e) {
      fail("Got unexpected IO exception");
    }
  }
}
