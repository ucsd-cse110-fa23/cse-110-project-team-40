package PantryPal;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javafx.event.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.Account;
import utils.Recipe;

/** Immutable class to store results from sending a transcript to NewRecipeAPI */
class TranscriptResults {
  public final Recipe recipe;
  public final List<String> prompts;

  TranscriptResults(JSONObject json) throws IllegalArgumentException {
    try {

      /* read prompts from jsonArray */
      this.prompts = new ArrayList<String>();
      JSONArray jsonArray = json.getJSONArray("transcript");
      for (int i = 0; i < jsonArray.length(); i++) {
        this.prompts.add(jsonArray.getString(i));
      }
      if (json.has("recipe")) {
        JSONObject recipeJson = json.getJSONObject("recipe");

        this.recipe = new Recipe(recipeJson);
      } else {
        this.recipe = null;
      }
    } catch (Exception e) {
      throw new IllegalArgumentException(
          "Input json was not valid (Error: " + e.getMessage() + ")");
    }
  }

  TranscriptResults() {
    this.recipe = null;
    this.prompts = new ArrayList<String>();
  }
}

/** Handles UI transitions made when interacting with NewRecipeAPI */
class NewRecipeController {
  private static final String stopButtonTitle = "Stop Recording";
  private static final String startButtonTitle = "Start Recording";
  private Account account;
  NewRecipePage newRecipePage;
  NewRecipeModel newRecipeModel;
  PageTracker pt;
  VoiceToText voiceToText;

  NewRecipeController(
      NewRecipePage newRecipePage,
      NewRecipeModel newRecipeModel,
      PageTracker pt,
      VoiceToText voicetotext,
      Account account) {
    this.newRecipePage = newRecipePage;
    this.newRecipeModel = newRecipeModel;
    this.pt = pt;
    this.voiceToText = voicetotext;
    this.account = account;
    init();
  }

  ScrollablePage getPage() {
    return this.newRecipePage;
  }

  /** handle all actions for start button */
  void start() {
    voiceToText.startRecording();
    gotoRecordingState();
  }

  void gotoRecordingState() {
    /* set correct button layout for this state */
    newRecipePage.footer.deleteButton(startButtonTitle);
    newRecipePage.footer.addButton(
        stopButtonTitle,
        e -> {
          this.stop();
        });
  }

  /** Setup for initial state (enter state machine) */
  void init() {
    /* poll transcript to get initial messages */
    TranscriptResults results;
    try {
      results = newRecipeModel.getInitialTranscript();
    } catch (Exception e) {
      System.err.println("error: " + e.getMessage());
      results = new TranscriptResults();
    }
    newRecipePage.setPrompts(results.prompts);

    /* setup buttons that persist all states */
    newRecipePage.footer.addButton(
        "home",
        e -> {
          this.exit();
        });
    /* go to waiting state */
    gotoWaitingState();
  }

  /** Handle all actions for stop button */
  void stop() {
    /* handle stop tasks */
    voiceToText.stopRecording();
    TranscriptResults results = sendVoiceToText();
    newRecipePage.setPrompts(results.prompts);

    /* finish setting up buttons based on state */
    if (results.recipe != null) {
      gotoRecipeCompleteState(results.recipe);
    } else {
      gotoWaitingState();
    }
  }

  /** Gets transcript from VoiceToText and passes it to server */
  TranscriptResults sendVoiceToText() {
    String transcript = voiceToText.getTranscript();
    TranscriptResults results;
    try {
      results = newRecipeModel.sendTranscript(transcript);
    } catch (Exception e) {
      System.err.println("error: " + e.getMessage());
      results = new TranscriptResults();
    }
    return results;
  }

  void gotoRecipeCompleteState(Recipe recipe) {
    newRecipePage.footer.deleteButton(stopButtonTitle);
    newRecipePage.footer.addButton(
        "view details",
        e -> {
          this.done(recipe);
        });
  }

  void gotoWaitingState() {
    newRecipePage.footer.deleteButton(stopButtonTitle);
    newRecipePage.footer.addButton(
        startButtonTitle,
        e -> {
          this.start();
        });
  }

  /** exit state machine to look at new recipe */
  void done(Recipe recipe) {
    newRecipeModel.reset();
    HttpRequestModel httpModel = new HttpRequestModel();
    httpModel.registerObserver(pt);
    NewRecipeDetailPage drp =
        new NewRecipeDetailPage(
            new RecipeDetailUI(recipe, new RecipeDetailModel(httpModel, account)));
    drp.footer.addButton(
        "home",
        e -> {
          pt.goHome();
        });
    pt.swapToPage(drp);
  }

  /** Exit handler for early exits from state machine */
  void exit() {
    newRecipeModel.reset();
    pt.goHome();
  }
}

class NewRecipeUI extends VBox {
  private void format() {
    this.setSpacing(20); // sets spacing between tasks
    this.setPrefSize(500, 560);
    this.setStyle("-fx-background-color: #F0F8FF;");
  }

  void addPrompt(String text) {
    Label label = new Label(text);
    label.setPrefSize(500, 20); // sets size of task
    label.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 0; -fx-font-weight: bold;");
    this.getChildren().add(label);
  }

  NewRecipeUI() {}

  void setPrompts(List<String> prompts) {
    this.getChildren().clear();
    for (String prompt : prompts) {
      addPrompt(prompt);
    }
  }
}

/** Page wrapper for NewRecipeUI */
public class NewRecipePage extends ScrollablePage {
  private NewRecipeUI newRecipeUI;

  NewRecipePage(NewRecipeUI newRecipeUI) {
    super("New Recipe", newRecipeUI);
    this.newRecipeUI = newRecipeUI;
  }

  void setPrompts(List<String> prompts) {
    newRecipeUI.setPrompts(prompts);
  }
}
