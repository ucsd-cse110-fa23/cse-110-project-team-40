package server;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import utils.Recipe;
import utils.VoiceToText;

class RecipeCreatorStub implements RecipeCreator {
  String recipe;

  public String makeRecipe(String meal, String ingredients) {
    return recipe;
  }

  public void setRecipe(String recipe) {
    this.recipe = recipe;
  }
}

class VoiceToTextStub implements VoiceToText {
  public Boolean waitingForMeal = true;
  String mealType;
  String ingredients;

  public void startRecording() {}

  public void stopRecording() {}

  public String getTranscript() {
    String response = waitingForMeal ? mealType : ingredients;
    waitingForMeal = false;
    return response;
  }

  public void setMealType(String mealType) {
    this.mealType = mealType;
  }

  public void setIngredients(String ingredients) {
    this.ingredients = ingredients;
  }
}

public class NewRecipeTest {

  RecipeCreatorStub recipeCreatorTest = new RecipeCreatorStub();

  NewRecipeCreator newRecipeCreatorTest = new NewRecipeCreator(recipeCreatorTest);

  @Test
  public void testHandleMealType() {
    boolean gotCorrectException = false;
    /* Ensuring calling with improper meal type does not advance state */
    try {
      newRecipeCreatorTest.handleMeal("snack");
    } catch (IllegalArgumentException e) {
      gotCorrectException = e.getMessage() == "Not a valid meal type";
    }
    assertEquals(gotCorrectException, true);
    assertEquals(newRecipeCreatorTest.waitingForMeal, true);
    /* Ensuring calling with proper meal type advances state */
    newRecipeCreatorTest.handleMeal("dinner");
    assertEquals(newRecipeCreatorTest.waitingForMeal, false);
  }

  @Test
  public void testInterpretRecipeResponse() {
    /* Checking against expected format received from ChatGPT */
    assertEquals(
        newRecipeCreatorTest
            .interpretRecipeResponse("Cereal\nPut milk into cereal")
            .getDescription(),
        new Recipe("Cereal", "breakfast", "Put milk into cereal").getDescription());
    /* strip off excess white space at end */
    assertEquals(
        newRecipeCreatorTest
            .interpretRecipeResponse("Tea\nPut teabag into hot water\n")
            .getDescription(),
        new Recipe("Tea", "breakfast", "Put teabag into hot water").getDescription());
    /*single line comment*/
    assertEquals(
        newRecipeCreatorTest.interpretRecipeResponse("Tea:Put teabag into hot water"), null);
  }

  @Test
  public void testUpdate() {
    String mealType = "dinner";
    ArrayList<String> expectedPrompts = new ArrayList<>();

    expectedPrompts.add("Would you like Breakfast, Lunch, or Dinner?");

    /* Prompt with meal */
    newRecipeCreatorTest.update(null, mealType);
    expectedPrompts.add(mealType);
    expectedPrompts.add("What ingredients do you have?");
    assertEquals(expectedPrompts, newRecipeCreatorTest.getPrompts());
    assertEquals(false, newRecipeCreatorTest.waitingForMeal);
    assertEquals(mealType, newRecipeCreatorTest.mealType);
    assertEquals(null, newRecipeCreatorTest.getRecipe());
  }

  @Test
  public void testNewRecipe() {
    /*Mocking and whole class test for New Recipe*/
    String mealType = "dinner";
    String ingredients = "instant noodles, water";
    String title = "Instant Noodles";
    String desc = "Add noodles to boiling water for two minutes";
    String rawRecipe = title + "\n" + desc;
    ArrayList<String> expectedPrompts = new ArrayList<>();

    expectedPrompts.add("Would you like Breakfast, Lunch, or Dinner?");

    /*check expected pre-meal state */
    assertEquals(expectedPrompts, newRecipeCreatorTest.getPrompts());
    assertEquals(null, newRecipeCreatorTest.getRecipe());
    assertEquals(true, newRecipeCreatorTest.waitingForMeal);

    /* Prompt with meal */
    newRecipeCreatorTest.readResponse(mealType);
    expectedPrompts.add(mealType);
    expectedPrompts.add("What ingredients do you have?");

    /* check expected for post meal state */
    assertEquals(expectedPrompts, newRecipeCreatorTest.getPrompts());
    assertEquals(false, newRecipeCreatorTest.waitingForMeal);
    assertEquals(mealType, newRecipeCreatorTest.mealType);
    assertEquals(null, newRecipeCreatorTest.getRecipe());

    /*prompt with ingredients */
    recipeCreatorTest.setRecipe(rawRecipe);
    newRecipeCreatorTest.readResponse(ingredients);

    /* check expected values for final state */
    expectedPrompts.add(ingredients);
    assertEquals(expectedPrompts, newRecipeCreatorTest.getPrompts());
    assertEquals(false, newRecipeCreatorTest.waitingForMeal);
    assertEquals(title, newRecipeCreatorTest.getRecipe().getTitle());
    assertEquals(mealType, newRecipeCreatorTest.getRecipe().getMealType());
    assertEquals(desc, newRecipeCreatorTest.getRecipe().getDescription());
  }
}
