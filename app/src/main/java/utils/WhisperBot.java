package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;
import utils.AudioRecorder;
import utils.ConfigReader;


/** Implementation of the VoiceToText interface using the OpenAI Whisper ASR API. */
public class WhisperBot implements VoiceToText {
  private static final String API_ENDPOINT = "https://api.openai.com/v1/audio/transcriptions";
  private static final String MODEL = "whisper-1";
  private static final String filePath = "output.wav";
  private String token;
  private String output;
  private AudioRecorder recorder;

  public WhisperBot() {
    ConfigReader configReader = new ConfigReader();
    token = configReader.getOpenAiApiKey();
    recorder = new AudioRecorder();
  }

  // Helper method to write parameters to the output stream
  public static void writeParameterToOutputStream(
      OutputStream outputStream, String parameterName, String parameterValue, String boundary)
      throws IOException {
    outputStream.write(("--" + boundary + "\r\n").getBytes());
    outputStream.write(
        ("Content-Disposition: form-data; name=\"" + parameterName + "\"\r\n\r\n").getBytes());
    outputStream.write((parameterValue + "\r\n").getBytes());
  }

  // Helper method to write a file to the output stream
  public static void writeInputToOutputStream(
      OutputStream outputStream, InputStream inputStream, String boundary) throws IOException {
    outputStream.write(("--" + boundary + "\r\n").getBytes());
    outputStream.write(
        ("Content-Disposition: form-data; name=\"file\"; filename=\"" + filePath + "\"\r\n")
            .getBytes());
    outputStream.write(("Content-Type: audio/mpeg\r\n\r\n").getBytes());

    byte[] buffer = new byte[1024];
    int bytesRead;
    while ((bytesRead = inputStream.read(buffer)) != -1) {
      outputStream.write(buffer, 0, bytesRead);
    }
    inputStream.close();
    // Implementation omitted for brevity
  }

  // Helper method to handle a successful API response
  public void handleSuccessResponse(HttpURLConnection connection)
      throws IOException, JSONException {
    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    String inputLine;
    StringBuilder response = new StringBuilder();
    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();

    JSONObject responseJson = new JSONObject(response.toString());

    String generatedText = responseJson.getString("text");

    output = generatedText;
  }

  // Helper method to handle an error response from the API
  public static void handleErrorResponse(HttpURLConnection connection)
      throws IOException, JSONException {
    BufferedReader errorReader =
        new BufferedReader(new InputStreamReader(connection.getErrorStream()));
    String errorLine;
    StringBuilder errorResponse = new StringBuilder();
    while ((errorLine = errorReader.readLine()) != null) {
      errorResponse.append(errorLine);
    }
    errorReader.close();
    String errorResult = errorResponse.toString();
    System.out.println("Error Result: " + errorResult);
  }
  public InputStream getClientInStream() {
    InputStream in;
    try {
      File file = new File(filePath);
      in = new FileInputStream(file);
      return in;
    } catch (Exception e) {
      System.err.println("failed opening file " + filePath);
      return null;
    }
  }
  /**
   * Get the transcript of the recorded audio using the OpenAI Whisper ASR API.
   *
   * @return Transcript of the recorded audio, or null if an error occurs.
   */
  public String getTranscript() {
    InputStream in = getClientInStream();
    if (in != null) {
      return getTranscript(in);
    } else {
      return null;
    }
  }

  public String getTranscript(InputStream in) {
    try {
      // Set up HTTP connection
      URL url = new URI(API_ENDPOINT).toURL();
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);

      // Set up request headers
      String boundary = "Boundary-" + System.currentTimeMillis();
      connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
      connection.setRequestProperty("Authorization", "Bearer " + token);

      // Set up output stream to write request body
      OutputStream outputStream = connection.getOutputStream();

      // Write model parameter to request body
      writeParameterToOutputStream(outputStream, "model", MODEL, boundary);
      // Open file
      File file = new File(filePath);
      // Write file parameter to request body
      writeInputToOutputStream(outputStream, in, boundary);

      // Write closing boundary to request body
      outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());

      // Flush and close output stream
      outputStream.flush();
      outputStream.close();

      // Get response code
      int responseCode = connection.getResponseCode();

      // Check response code and handle response accordingly
      if (responseCode == HttpURLConnection.HTTP_OK) {
        handleSuccessResponse(connection);
      } else {
        handleErrorResponse(connection);
      }

      // Disconnect connection
      connection.disconnect();

    } catch (IOException | URISyntaxException e) {
      return null;
    }
    return output;
  }

  public String getOutput() {
    return output;
  }

  /** Start recording audio using the AudioRecorder instance. */
  public void startRecording() {
    recorder.start();
  }

  /** Stop recording audio using the AudioRecorder instance. */
  public void stopRecording() {
    recorder.stop();
  }
}

/** Mock implementation of the VoiceToText interface for testing purposes. */
class MockWhisperer implements VoiceToText {
  // Instance variable for storing the mock transcript
  private String mockTranscript;
  public boolean isRecording;

  public void startRecording() {
    isRecording = true;
  }

  public void stopRecording() {
    isRecording = false;
  }

  /**
   * Returns a mock transcript for testing purposes.
   *
   * @return A string containing a fake transcript.
   */
  public String getTranscript() {
    return mockTranscript;
  }

  /**
   * Sets the mockTranscript instance variable.
   *
   * @param mockTranscript The mock transcript to be set.
   */
  public void setOutput(String mockTranscript) {
    this.mockTranscript = mockTranscript;
  }
}
