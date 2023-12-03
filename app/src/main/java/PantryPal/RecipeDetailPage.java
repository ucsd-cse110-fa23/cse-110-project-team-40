package PantryPal;

import java.io.*;
import javafx.event.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.text.TextAlignment;
import utils.Recipe;

/**
 * RecipeDetailPage provides a user interface to display the details of a recipe. It integrates with
 * RecipeDetailUITemplate to present recipe details that can include both non-editable and editable
 * fields, along with associated actions as footer buttons. (depends on which variant UI out of the
 * 3 is used)
 *
 * <p>Responsibilities: - Displays the title of the recipe using the Header component. - Adds an
 * "Exit" button to the header for navigation purposes. - Aggregates and displays footer buttons
 * provided by RecipeDetailUITemplate.
 *
 * <p>Usage: - An instance of this class should be created by passing a RecipeDetailUITemplate which
 * should be a specific variant of UI among RecipeDetailUI, EditableRecipeUI, or
 * NewDetailedRecipeUI. - exit method is placeholder for tab switch to RecipeListPage.
 */
public class RecipeDetailPage extends ScrollablePage {
  RecipeDetailUI recipeDetailUI;

  RecipeDetailPage(RecipeDetailUI recipeDetailUI) {
    super("Recipe Detail", recipeDetailUI); // This initializes and adds the header.
    this.recipeDetailUI = recipeDetailUI;
    this.footer.addButton(
        "delete",
        e -> {
          recipeDetailUI.delete();
        });
    this.footer.addButton(
        "edit",
        e -> {
          edit();
        });
  }

  public void edit() {
    /* set correct buttons for current state */
    this.footer.deleteButton("edit");
    this.footer.addButton(
        "save",
        e -> {
          save();
        });

    recipeDetailUI.setDescriptionEditable(true);
  }

  public void save() {
    /* set correct buttons for current state */
    this.footer.deleteButton("save");
    this.footer.addButton(
        "edit",
        e -> {
          edit();
        });

    recipeDetailUI.setDescriptionEditable(false);

    recipeDetailUI.save();
  }
}

class NewRecipeDetailPage extends ScrollablePage {
  RecipeDetailUI recipeDetailUI;

  NewRecipeDetailPage(RecipeDetailUI recipeDetailUI) {
    super("Recipe Detail", recipeDetailUI); // This initializes and adds the header.
    this.recipeDetailUI = recipeDetailUI;
    this.footer.addButton(
        "save",
        e -> {
          recipeDetailUI.save();
        });
  }
}

/**
 * RecipeDetailUITemplate serves as the abstract base for UI components that represent the details
 * of a recipe within the user interface.
 *
 * <p>Common Properties: - All derived classes accept a RecipeList and a Recipe object in their
 * constructor. - Title field is always present but not user-editable. - Description field's
 * editability can be toggled. - Supports adding different buttons to a footer area.
 *
 * <p>Derived Classes: - RecipeDetailUI: Includes functionality to delete and edit a recipe. -
 * EditableRecipeUI: Includes functionality to delete a recipe and save changes. -
 * NewDetailedRecipeUI: Includes functionality to save a new recipe.
 */
class RecipeDetailUI extends VBox {
  protected Recipe recipe;
  protected Label titleField;
  protected TextArea descriptionField;
  protected Label mealTypeField;
  protected ImageView imageView;
  private RecipeDetailModel recipeDetailModel;
  private ImageModel imageModel;

  public void format() {
    /* format title field */
    titleField.setPrefSize(600, 20);
    titleField.setStyle("-fx-background-color: #dae5ea; -fx-border-width: 0;");
    titleField.setTextAlignment(TextAlignment.LEFT);
    titleField.setText(recipe.getTitle());

    /* format description field */
    descriptionField.setPrefSize(600, 600);
    descriptionField.setEditable(false); // Start as non-editable
    descriptionField.setText(recipe.getDescription());
    descriptionField.setWrapText(true);
    setDescriptionEditable(false);

    /*format meal type tag */
    mealTypeField.setPrefSize(600, 10);
    mealTypeField.setStyle("-fx-background-color: #dae5ea; -fx-border-width: 0;");
    mealTypeField.setTextAlignment(TextAlignment.CENTER);
    mealTypeField.setText(recipe.getMealType());

    /* format recipe image */
    try {
      Image image = imageModel.getImage(recipe.getTitle());
      imageView.setImage(image);
      imageView.setFitWidth(200); // Set preferred width
      imageView.setFitHeight(200); // Set preferred height
      imageView.setPreserveRatio(true);
    } catch (Exception e) {
      System.err.println("Image file generation failed: " + e.getMessage());
    }
    // Optionally set a placeholder image or handle the error
  }

  RecipeDetailUI(Recipe recipe, RecipeDetailModel recipeDetailModel, ImageModel imageModel) {
    this.recipe = recipe;
    this.recipeDetailModel = recipeDetailModel;
    this.imageModel = imageModel;

    // Initialize title and description fields
    titleField = new Label();
    titleField.setText(recipe.getTitle());
    descriptionField = new TextArea();
    descriptionField.setWrapText(true);
    mealTypeField = new Label();
    mealTypeField.setText(recipe.getMealType());
    imageView = new ImageView();

    this.format();
    this.getChildren().add(titleField);
    this.getChildren().add(descriptionField);
    this.getChildren().add(mealTypeField);
    this.getChildren().add(imageView);
  }

  public void save() {
    recipeDetailModel.update(
        new Recipe(titleField.getText(), mealTypeField.getText(), descriptionField.getText()));
  }

  public void delete() {
    recipeDetailModel.delete(titleField.getText());
    getChildren().clear();
  }

  // to set whether the description is editable
  public void setDescriptionEditable(boolean editable) {
    descriptionField.setEditable(editable);
  }
}
