/* Code initially adapted from Lab1 */

package PantryPal;

import java.io.*;
import javafx.event.*;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import utils.Account;
import utils.Recipe;

interface LinkMaker {
  String makeLink(String title, Account account);
}

class ShareLinkMaker implements LinkMaker {
  public static final String BASEURL = "http://localhost";
  public static final String PORT = "8100";

  public String makeLink(String title, Account account) {
    return ShareLinkMaker.BASEURL
        + ":"
        + ShareLinkMaker.PORT
        + "/share/"
        + account.getUsername()
        + "/"
        + Recipe.sanitizeTitle(title);
  }
}

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

/** UI Page containing share link */
public class SharePage extends ScrollablePage {
  SharePage(String title) {
    super("Share", new ShareUI(title));
  }
}
