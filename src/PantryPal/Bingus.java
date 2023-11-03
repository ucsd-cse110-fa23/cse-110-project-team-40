package PantryPal;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

interface IRecipeCreator {
    /* returns string response, or null if error */
   public String makeRecipe(String meal, String ingredients);
}

public class Bingus implements IRecipeCreator {
    private static final String API_ENDPOINT = "https://api.openai.com/v1/completions";
    private static final String API_KEY = "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";
    private static final String MODEL = "text-davinci-003";
    public String bingusOutput;

    public String makeRecipe(String meal, String ingredients) {
        try {
            //String prompt = "waht is the velocty in agile development?";
            String prompt = "Give me a " + meal + " recipe using the following ingredients: " + ingredients + ". Thank you.";
            System.out.println(prompt);
            int maxTokens = 100;
            //int maxTokens = Integer.parseInt();

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

            // parse
            JSONObject responseJson = new JSONObject(responseBody.body());
            JSONArray choices = responseJson.getJSONArray("choices");
            String generatedText = choices.getJSONObject(0).getString("text");
            bingusOutput = generatedText;

        } catch(Exception e) {
            // How can u possibly screw this up HmmHMHmmMMMmMHMmmmMmMMmmmm 🤔
            return null;
        }

        return bingusOutput;
    }

}

class mockBingus implements IRecipeCreator {
    public String makeRecipe(String meal, String ingredients) {
        return "This is an example of the expected output from chatgpt." + 
               "\nThese are your inputs: " + meal + " and " + ingredients;
    }
}