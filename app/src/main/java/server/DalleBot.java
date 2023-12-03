package server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utils.ConfigReader;

interface ImageGenerator {
  String generateImage(String recipeName);
}

/**
 * generates an image using the OpenAI DALL-E API, stored locally under., called by RecipeDetail Ui
 */
public class DalleBot implements ImageGenerator {
  private static final String API_ENDPOINT = "https://api.openai.com/v1/images/generations";
  private static final String MODEL = "dall-e-2";
  private static final String basePath = "out/PantryPal/recipeImages/";
  private String token;
  private String username;
  private String directory;

  public DalleBot(String bdirectory) {
    this.directory = directory;
    this.username = username;
    directory = basePath + username + "/";
    ConfigReader configReader = new ConfigReader();
    token = configReader.getOpenAiApiKey();
  }

  public String generateImage(String recipeName) {
    String prompt = recipeName;
    JSONObject requestBody = this.createRequestBody(1, "256x256", prompt);
    HttpClient client = HttpClient.newHttpClient();

    // add error handling
    String imagePath = this.getImagePath(recipeName);

    HttpResponse<String> response = this.sendRequest(client, requestBody);
    String generatedImageURL = this.processResponse(response);
    this.downloadImage(generatedImageURL, imagePath);
    return imagePath;
  }

  public void makeDirectory() {
    new File(directory).mkdirs();
  }

  public String getImagePath(String recipeName) {
    String imagePath = directory + recipeName + ".jpg";
    // remove special characters from recipe name, change spaces to underscores
    imagePath = recipeName.replaceAll("[^a-zA-Z0-9]", "_");
    return imagePath;
  }

  JSONObject createRequestBody(int n, String size, String prompt) {
    JSONObject requestBody = new JSONObject();
    requestBody.put("model", MODEL);
    requestBody.put("prompt", prompt);
    requestBody.put("n", n);
    requestBody.put("size", size);
    return requestBody;
  }

  private HttpResponse<String> sendRequest(HttpClient client, JSONObject requestBody) {
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(API_ENDPOINT))
            .header("Content-Type", "application/json")
            .header("Authorization", String.format("Bearer %s", token))
            .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
            .build();
    HttpResponse<String> response = null;
    try {
      response = client.send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
    return response;
  }

  String processResponse(HttpResponse<String> response) {
    String responseBody = response.body();

    JSONObject responseJson = new JSONObject(responseBody);

    // Check if the response contains the "data" key
    if (!responseJson.has("data")) {
      throw new JSONException("Response does not contain 'data' key");
    }

    JSONArray dataArray = responseJson.getJSONArray("data");
    return dataArray.getJSONObject(0).getString("url");
  }

  private void downloadImage(String imageURL, String path) {
    try {
      InputStream in = new URI(imageURL).toURL().openStream();
      Files.copy(in, Paths.get(path));
      in.close();
    } catch (URISyntaxException e) {
      System.err.println("Invalid URL syntax: " + imageURL);
      e.printStackTrace();
    } catch (IOException e) {
      System.err.println("I/O Error when saving the image: " + e.getMessage());
      e.printStackTrace();
    }
  }
}

/** Mock class for testing generateImage path returned */
/** Mock class for testing DalleBot functionality */
class MockDalleBot implements ImageGenerator {
  private boolean shouldThrowException;
  private String fixedResponsePath;

  public MockDalleBot() {
    // Default behavior
    this.shouldThrowException = false;
    this.fixedResponsePath = "out/PantryPal/recipeImages/mockImage.jpg";
  }

  public void setShouldThrowException(boolean shouldThrowException) {
    this.shouldThrowException = shouldThrowException;
  }

  public void setFixedResponsePath(String path) {
    this.fixedResponsePath = path;
  }

  @Override
  public String generateImage(String recipeName) {
    if (shouldThrowException) {
      throw new RuntimeException("Simulated exception in generateImage");
    }

    // Generate a mock path based on the recipe name
    String mockImagePath =
        fixedResponsePath.equals("")
            ? "out/PantryPal/recipeImages/" + recipeName.replaceAll("[^a-zA-Z0-9]", "_") + ".jpg"
            : fixedResponsePath;

    return mockImagePath;
  }
}
