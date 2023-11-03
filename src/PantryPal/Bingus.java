package PantryPal;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class Bingus {
    private static final String API_ENDPOINT = "https://api.openai.com/v1/completions";
    private static final String API_KEY = "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";
    private static final String MODEL = "text-davinci-003";
    
    public static void main(String[] args) throws Exception {

        //String prompt = "waht is the velocty in agile development?";
        String prompt = args[1];
        System.out.println(prompt);
        //int maxTokens = 100;
        int maxTokens = Integer.parseInt(args[0]);
        System.out.println("\nChadGPT response:");

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
        System.out.println(generatedText);

    }
}

