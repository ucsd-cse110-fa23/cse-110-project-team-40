package server;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import javafx.event.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import utils.Recipe;

/** Uses a state machine approach to pass control between methods when creating new recipe */
public class NewRecipeCreator implements InteractiveRecipeMaker {
  String mealType;
  Boolean waitingForMeal;
  RecipeCreator recipeCreator;
  public List<String> prompts;
  private Recipe recipe;

  public void update(Observable o, Object whisperData) {
    System.err.println("NewRecipeCreator updated");
    readResponse((String) whisperData);
  }

  NewRecipeCreator(RecipeCreator recipeCreator) {
    this.recipeCreator = recipeCreator;
    reset();
  }

  public void reset() {
    recipe = null;
    waitingForMeal = true;
    this.prompts = new ArrayList<String>();
    prompts.add("Would you like Breakfast, Lunch, or Dinner?");
  }

  public void readResponse(String response) throws IllegalArgumentException {
    if (response == null) {
      return;
    }
    if (waitingForMeal) {
      handleMeal(response);
    } else {
      handleIngredients(response);
    }
  }

  public Recipe getRecipe() {
    return recipe;
  }

  public List<String> getPrompts() {
    return prompts;
  }

  /**
   * Checks whether meal type is valid before changing state to accept ingredients or reasking for
   * meal type
   */
  public void handleMeal(String response) throws IllegalArgumentException {
    response = response.toLowerCase();
    waitingForMeal = true;
    if (response.contains("breakfast")) {
      mealType = "breakfast";
    } else if (response.contains("lunch")) {
      mealType = "lunch";
    } else if (response.contains("dinner")) {
      mealType = "dinner";
    } else {
      prompts.add("Please re-specify meal type.");
      throw new IllegalArgumentException("Not a valid meal type");
    }
    // Transition to waiting for ingredients state
    prompts.add(mealType);
    prompts.add("What ingredients do you have?");
    waitingForMeal = false;
  }

  /*
   * Divides recipes into title and body given a multi
   * line recipe
   * Drops extra new lines at end!
   * */
  public Recipe interpretRecipeResponse(String response) {
    String[] responseLines = response.split("\n");
    String instructions = "";
    int titleLineIndex = 0;
    /* if their is no content, return null */
    if (responseLines.length == 0) {
      return null;
    }

    /* title is first nonempty line */
    for (; titleLineIndex < responseLines.length; titleLineIndex++) {
      if (responseLines[titleLineIndex] != "") {
        break;
      }
    }
    /* if their is no description, return null */
    if (titleLineIndex == responseLines.length - 1) {
      return null;
    }
    /* any further lines are description */
    for (int i = titleLineIndex + 1; i < responseLines.length; i++) {
      instructions += responseLines[i] + "\n";
    }
    return new Recipe(responseLines[titleLineIndex].trim(), mealType, instructions.trim());
  }

  /*Hands the ingredients to chat gpt and creates a recipe object using text response from chatgpt*/
  public void handleIngredients(String response) {
    prompts.add(response);
    String recipeResponse = recipeCreator.makeRecipe(mealType, response);
    recipe = interpretRecipeResponse(recipeResponse);
  }
}
