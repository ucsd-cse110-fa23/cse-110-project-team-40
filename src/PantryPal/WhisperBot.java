package PantryPal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Interface for voice-to-text functionality.
 */
interface IVoiceToText {
    /**
     * Start recording audio.
     */
    public void startRecording();
    
    /**
     * Stop recording audio.
     */
    public void stopRecording();
    
    /**
     * Retrieve the transcript of the recorded audio.
     * 
     * @return Transcript of the recorded audio, or null if an error occurs.
     */
    public String getTranscript();
}

/**
 * Implementation of the IVoiceToText interface using the OpenAI Whisper ASR API.
 */
public class WhisperBot implements IVoiceToText {
    // Constants for API and authentication
    private static final String API_ENDPOINT = "https://api.openai.com/v1/audio/transcriptions";
    private static final String TOKEN = "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";
    private static final String MODEL = "whisper-1";
    // File path for recording output
    private static String FILE_PATH;
    public String output;

    // Audio recorder instance
    private AudioRecorder recorder = new AudioRecorder();

    // Helper method to write parameters to the output stream
    private static void writeParameterToOutputStream(
            OutputStream outputStream,
            String parameterName,
            String parameterValue,
            String boundary
    ) throws IOException {
        outputStream.write(("--" + boundary + "\r\n").getBytes());
        outputStream.write(
    (
        "Content-Disposition: form-data; name=\"" + parameterName + "\"\r\n\r\n"
    ).getBytes()
    );
        outputStream.write((parameterValue + "\r\n").getBytes());
    }

    // Helper method to write a file to the output stream
    private static void writeFileToOutputStream(
        OutputStream outputStream,
        File file,
        String boundary
    ) throws IOException {
        outputStream.write(("--" + boundary + "\r\n").getBytes());
        outputStream.write(
        (
            "Content-Disposition: form-data; name=\"file\"; filename=\"" +
            file.getName() +
            "\"\r\n"
            ).getBytes()
        );
        outputStream.write(("Content-Type: audio/mpeg\r\n\r\n").getBytes());

        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        fileInputStream.close();
        // Implementation omitted for brevity
    }

    // Helper method to handle a successful API response
    private void handleSuccessResponse(HttpURLConnection connection)
    throws IOException, JSONException {
        BufferedReader in = new BufferedReader(
            new InputStreamReader(connection.getInputStream())
        );
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
    private static void handleErrorResponse(HttpURLConnection connection)
    throws IOException, JSONException {
        BufferedReader errorReader = new BufferedReader(
            new InputStreamReader(connection.getErrorStream())
        );
        String errorLine;
        StringBuilder errorResponse = new StringBuilder();
        while ((errorLine = errorReader.readLine()) != null) {
            errorResponse.append(errorLine);
        }
        errorReader.close();
        String errorResult = errorResponse.toString();
        System.out.println("Error Result: " + errorResult);
    }

    /**
     * Get the transcript of the recorded audio using the OpenAI Whisper ASR API.
     * 
     * @return Transcript of the recorded audio, or null if an error occurs.
     */
    public String getTranscript() {
        try {
            // Set up the file path and create a file object
            FILE_PATH = "output.wav";
            System.out.println("\nWhisper Transcription:");

            // Create file object from file path
            File file = new File(FILE_PATH);

            // Set up HTTP connection
            URL url = new URI(API_ENDPOINT).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);


            // Set up request headers
            String boundary = "Boundary-" + System.currentTimeMillis();
            connection.setRequestProperty(
                "Content-Type",
                "multipart/form-data; boundary=" + boundary
            );
            connection.setRequestProperty("Authorization", "Bearer " + TOKEN);

            
            // Set up output stream to write request body
            OutputStream outputStream = connection.getOutputStream();


            // Write model parameter to request body
            writeParameterToOutputStream(outputStream, "model", MODEL, boundary);


            // Write file parameter to request body
            writeFileToOutputStream(outputStream, file, boundary);


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

    /**
     * Start recording audio using the AudioRecorder instance.
     */
    public void startRecording() {
        recorder.start();
    }

    /**
     * Stop recording audio using the AudioRecorder instance.
     */
    public void stopRecording() {
        recorder.stop();
    }
}

/**
 * Mock implementation of the IVoiceToText interface for testing purposes.
 */
class MockWhisperer implements IVoiceToText {
    // Instance variable for storing the mock transcript
    private String mockTranscript;
    
    /**
     * Starts the recording process.
     * Prints a message to the console to simulate the start of a recording.
     */
    public void startRecording() {
        System.out.println("Recording started... Beep Boop Beep Boop");
    }
    
    /**
     * Stops the recording process.
     * Prints a message to the console to simulate the end of a recording.
     */
    public void stopRecording() {
        System.out.println("Recording stopped... YAAAAAAAAY");
    }
    
    /**
     * Returns a mock transcript for testing purposes.
     *
     * @return A string containing a fake transcript.
     */
    public String getTranscript() {
        return "This is a fake transcript for testing.";
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

/**
 * Class responsible for recording audio using the Java Sound API.
 */
class AudioRecorder {
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    private boolean isRecording;

    /**
     * Constructor to set up the audio format.
     */
    public AudioRecorder() {
        audioFormat = new AudioFormat(44100, 16, 2, true, false);
    }

    /**
     * Start recording audio.
     */
    public void start() {
        if (isRecording) {
            System.out.println("Already recording.");
            return;
        }

        try {
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            isRecording = true;

            new Thread(() -> {
                try {
                    AudioInputStream audioInputStream = new AudioInputStream(targetDataLine);
                    AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new java.io.File("output.wav"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop recording audio.
     */
    public void stop() {
        if (!isRecording) {
            System.out.println("Not currently recording.");
            return;
        }

        try {
            targetDataLine.stop();
            targetDataLine.close();
            isRecording = false;
            // You can convert the recorded WAV to MP3 using a different library if needed
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
}
