/* Code initially adapted from Lab1 */

package PantryPal;

import java.io.*;
import java.util.Base64;
import javafx.event.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import org.json.JSONObject;
import utils.Account;
import utils.Recipe;

/** Communication model for making API requests to get Recipe List. */
public class ImageModel {
  HttpModel httpModel;

  ImageModel(HttpModel httpModel, Account account) {
    this.httpModel = httpModel;
    httpModel.setPath("image/" + account.getUsername() + "/");
  }

  public InputStream decodeResponse(String response) {
    JSONObject json = new JSONObject(response);
    String data = json.getString("image");
    byte[] decodedData = Base64.getDecoder().decode(data);
    return new ByteArrayInputStream(decodedData);
  }

  public Image getImage(String recipeTitle) {
    String response = httpModel.performRequest("GET", Recipe.sanitizeTitle(recipeTitle), null);
    InputStream decoded = decodeResponse(response);
    return new Image(decoded);
  }
}
