/* Code initially adapted from Lab1 */

package PantryPal;

import java.util.List;
import javafx.scene.layout.VBox;

/** UI element which displays IMMUTABLE list of recipes */
class RecipeListUI extends VBox {
  RecipeListUI(List<RecipeEntryUI> entries) {
    this.getChildren().addAll(entries);
    format();
  }

  private void format() {
    this.setSpacing(5); // sets spacing between tasks
    this.setPrefSize(500, 560);
    this.setStyle("-fx-background-color: #F0F8FF;");
  }
}

/** UI Page containing recipe list, and accompanying header and footer */
public class RecipeListPage extends ScrollablePage {
  private String filter;
  private String sort;

  RecipeListPage(List<RecipeEntryUI> entries) {
    super("Recipe List", new RecipeListUI(entries));
    this.filter = "No filter";
    this.sort = "Newest";
  }

  public void updateFilter(String mealType) {
    this.filter = mealType;
  }

  public void updateSort(String sort) {
    this.sort = sort;
  }
}
