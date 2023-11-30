/* Code initially adapted from Lab1 */

package PantryPal;

import java.io.*;
import java.util.List;
import javafx.event.*;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.text.*;

/** UI element which displays share link */
class ShareUI extends VBox {
  TextArea link;
  ShareUI(String title) {
    this.link = new TextArea(title);
    this.getChildren().add(link);
    format();
  }

  private void format() {
    this.setSpacing(5); // sets spacing between tasks
    this.setPrefSize(500, 560);
    this.setStyle("-fx-background-color: #F0F8FF;");
  }
}

/** UI Page containing share link*/
public class SharePage extends ScrollablePage {
  SharePage(String title) {
    super("Share", new ShareUI(title));
  }
}
