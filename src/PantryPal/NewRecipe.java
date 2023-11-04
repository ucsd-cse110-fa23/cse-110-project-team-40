package PantryPal;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.application.Application;
import javafx.event.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import java.lang.Object;

interface RecipeCreator{
    public String makeRecipe(String meal, String ingredients); 
}

interface VoiceToText{
    public void startRecording ();
    public void stopRecording ();
    public String getTranscript();
}

class RecipeCreatorStub implements RecipeCreator {
    public String makeRecipe(String meal, String ingredients){
        return "Sandwich\n Add mayonaise";
    }
}
class VoiceToTextStub implements VoiceToText{
    public Boolean waitingForMeal = true;
    public void startRecording(){}
    public void stopRecording(){}
    public String getTranscript(){String response = waitingForMeal ? "breakfast" : "mayonaise";
        waitingForMeal = false;
        return response;
    }
}

class NewRecipeCreator{
    String mealType;
    Boolean waitingForMeal = true;
    VoiceToText voiceToText;
    RecipeCreator recipeCreator;
    public List<String> prompts;
    //PageTracker pageTracker
    
    NewRecipeCreator(VoiceToText voiceToText, RecipeCreator recipeCreator){
        this.voiceToText = voiceToText;
        this.recipeCreator = recipeCreator;
        this.prompts = new ArrayList<String>();
        prompts.add("Would you like Breakfast, Lunch, or Dinner?");
    }

    public void start(){
        voiceToText.startRecording();
    }

    public void stop(){
        voiceToText.stopRecording();
        String response = voiceToText.getTranscript();
        if (response == null){
            return;
        }
        if (waitingForMeal) {
            handleMeal(response);
        }
        else{
            handleIngredients(response);
        }
    
        // initiate the state transition, do something with the response 
    }

    public void handleMeal(String response){
        response = response.toLowerCase();
        waitingForMeal = true;
        System.out.println("meal triggered");
        if (response.contains("breakfast")){
            mealType = "breakfast";
        }
        else if (response.contains("lunch")){
            mealType = "lunch";
        }
        else if (response.contains("dinner")){
            mealType = "dinner";
            System.out.println("dinner triggered");
        }
        else{
            return;
        }
        //Transition to waiting for ingredients state
        prompts.add(mealType);
        prompts.add("What ingredients do you have");
        System.out.println("meal flag switch triggered");
        waitingForMeal = false;
    }
    public Recipe interpretRecipeResponse(String response){
        int delineator = response.indexOf("\n");
        String title = response.substring(0,delineator);
        String instructions = response.substring(delineator+1,response.length());
        return new Recipe(title, instructions);
    }

    private void handleIngredients(String response){
        String recipeResponse = recipeCreator.makeRecipe(mealType,response);
        Recipe recipe = interpretRecipeResponse(recipeResponse);
        //create detailed recipe page
        //pageTracker.swapToPage(detailedNewRecipePage);
    }
}

class NewRecipeUI extends VBox{
    NewRecipeCreator newRecipeCreator;

    private void format() {
		this.setSpacing(20); // sets spacing between tasks
		this.setPrefSize(500, 560);
		this.setStyle("-fx-background-color: #F0F8FF;");
        
	}
    void addPrompt(String text){
        Label label = new Label(text);
        label.setPrefSize(500, 20); // sets size of task
		label.setStyle(
		    "-fx-background-color: #DAE5EA; -fx-border-width: 0; -fx-font-weight: bold;");
        this.getChildren().add(label);
    }

    

    NewRecipeUI() {
		this.newRecipeCreator = new NewRecipeCreator(null, null);
        getNewPrompts();
	}
    void start(){
        newRecipeCreator.start();
    }
    void stop(){
        newRecipeCreator.stop();
        getNewPrompts();
    }
    void getNewPrompts(){
        this.getChildren().clear();
        for(int i = 0; i < newRecipeCreator.prompts.size(); i++){
            addPrompt(newRecipeCreator.prompts.get(i));
        }
    }
    
}

class NewRecipePage extends ScrollablePage{
    private NewRecipeUI newRecipeUI;
    private void addStopButton(){
        footer.addButton("Stop Recording", e-> {
            newRecipeUI.stop();
            footer.deleteButton("Stop Recording");
            addStartButton();
        });
    }
    private void addStartButton(){
        footer.addButton("Start Recording", e-> {
            newRecipeUI.start();
            footer.deleteButton("Start Recording");
            addStopButton();
        });
    }

    
    NewRecipePage() {
		super("New Recipe",
		    new NewRecipeUI());
        newRecipeUI = (NewRecipeUI) center;
        addStartButton();
        
    }
}




