package PantryPal;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An interface for creating recipes.
 */
interface IRecipeCreator {
    /**
     * Method for generating a recipe.
     *
     * @param meal The type of meal for the recipe.
     * @param ingredients The list of ingredients for the recipe.
     * @return A string response containing the generated recipe, or null if there is an error.
     */
    public String makeRecipe(String meal, String ingredients);
}

/**
 * The ChatGPTBot class implements the IRecipeCreator interface and provides a way to generate recipes
 * using the OpenAI API.
 */
public class ChatGPTBot implements IRecipeCreator {
    // Constants for API access
    private static final String API_ENDPOINT = "https://api.openai.com/v1/completions";
    private static final String API_KEY = "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";
    private static final String MODEL = "text-davinci-003";
    public String ChatGPTBotOutput;  // Store the generated recipe

    /**
     * Generates a recipe based on the given meal and ingredients.
     *
     * @param meal The type of meal for the recipe.
     * @param ingredients The list of ingredients for the recipe.
     * @return A string containing the generated recipe, or null if there is an error.
     */
    public String makeRecipe(String meal, String ingredients) {
        try {
            // Create a prompt for generating a recipe based on the meal and ingredients
            String prompt = "Give me a " + meal + " recipe using the following ingredients: " + ingredients + ". Thank you.";
            System.out.println(prompt);

            // Maximum number of tokens for the response
            int maxTokens = 100;

            // Create a JSON request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", MODEL);
            requestBody.put("prompt", prompt);
            requestBody.put("max_tokens", maxTokens);
            requestBody.put("temperature", 1.0);

            // Create the HTTP Client
            HttpClient client = HttpClient.newHttpClient();

            // Create the request object
            HttpRequest request = HttpRequest
            .newBuilder()
            .uri(new URI(API_ENDPOINT))
            .header("Content-Type", "application/json")
            .header("Authorization", String.format("Bearer %s", API_KEY))
            .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
            .build();

            // Send the request and receive the response
            HttpResponse<String> responseBody = client.send(
            request,
            HttpResponse.BodyHandlers.ofString()
            );

            // Parse the JSON response
            JSONObject responseJson = new JSONObject(responseBody.body());
            JSONArray choices = responseJson.getJSONArray("choices");
            String generatedText = choices.getJSONObject(0).getString("text");
            ChatGPTBotOutput = generatedText;

        } catch(Exception e) {
            // How can u possibly screw this up HmmHMHmmMMMmMHMmmmMmMMmmmm 🤔
            return null;  // Return null in case of an error
        }

        return ChatGPTBotOutput;
    }
}

/**
 * The mockChatGPTBot class implements the IRecipeCreator interface and provides a mock implementation
 * for testing purposes.
 */
class mockChatGPTBot implements IRecipeCreator {
    // Instance variables
    private String mockMeal;
    private String mockIngredients;
    /**
     * A mock implementation of the makeRecipe method.
     *
     * @param meal The type of meal for the mock recipe.
     * @param ingredients The list of ingredients for the mock recipe.
     * @return A string containing a mock recipe output.
     */
    public String makeRecipe(String meal, String ingredients) {
        return "This is an example of the expected output from ChatGPT." + 
               "\nThese are your inputs: " + meal + " and " + ingredients;
    }

    /**
 * Sets the mockMeal and mockIngredients instance variables.
 *
 * @param mockMeal The type of mock meal to be set.
 * @param mockIngredients The list of mock ingredients to be set.
 */
    public void setOutput(String mockMeal, String mockIngredients) {
        this.mockMeal = mockMeal;
        this.mockIngredients = mockIngredients;
    }

}
