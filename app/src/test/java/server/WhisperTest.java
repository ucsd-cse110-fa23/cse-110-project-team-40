package server;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.AudioRecorder;

public class WhisperTest {
  WhisperBot whisper;
  private ByteArrayOutputStream outputStream;

  @BeforeEach
  public void setUp() {
    outputStream = new ByteArrayOutputStream();
    whisper = new WhisperBot();
  }

  @Test
  public void testWriteParameterToOutputStream() throws IOException {
    String parameterName = "paramName";
    String parameterValue = "paramValue";
    String boundary = "boundary";

    WhisperBot.writeParameterToOutputStream(outputStream, parameterName, parameterValue, boundary);
    outputStream.close();

    String expectedOutput =
        "--boundary\r\n"
            + "Content-Disposition: form-data; name=\"paramName\"\r\n\r\n"
            + "paramValue\r\n";

    String actualOutput = new String(outputStream.toByteArray());

    assertEquals(expectedOutput, actualOutput);
  }

  @Test
  public void testWriteInputToOutputStream() throws IOException {
    String boundary = "boundary";
    File testFile = new File("src/test/java/PantryPal/testfile.txt");

    WhisperBot.writeInputToOutputStream(outputStream, new FileInputStream(testFile), boundary);
    outputStream.close();

    String expectedOutputStart =
        "--boundary\r\n"
            + "Content-Disposition: form-data; name=\"file\"; filename="
            + "\""
            + AudioRecorder.filePath
            + "\"\r\n"
            + "Content-Type: audio/mpeg\r\n\r\n";

    // Verify the file content
    FileInputStream fileInputStream = new FileInputStream(testFile);
    byte[] fileContent = new byte[(int) testFile.length()];
    fileInputStream.read(fileContent);
    fileInputStream.close();

    byte[] actualOutput = outputStream.toByteArray();

    // Compare the expected start of the output with the actual output
    assertArrayEquals(expectedOutputStart.getBytes(), actualOutput);

    // Compare the file content with the actual output starting from the end of the
    // expected output
    for (int i = 0; i < fileContent.length; i++) {
      assertEquals(fileContent[i], actualOutput[expectedOutputStart.length() + i]);
    }
  }

  @Test
  public void testHandleSuccessResponse() throws IOException {
    // Create a mock HttpURLConnection with a predefined response
    String jsonResponse = "{\"text\":\"Hello\"}";
    ByteArrayInputStream responseStream = new ByteArrayInputStream(jsonResponse.getBytes());
    HttpURLConnection mockConnection = new MockHttpURLConnection(responseStream);

    // Create an instance of WhisperBot and invoke the method
    WhisperBot whisperer = new WhisperBot();
    assertEquals("Hello", whisperer.handleSuccessResponse(mockConnection));
  }
}

// A simple mock for HttpURLConnection to provide a predefined response stream
class MockHttpURLConnection extends HttpURLConnection {
  private final InputStream responseStream;

  protected MockHttpURLConnection(InputStream responseStream) {
    super(null);
    this.responseStream = responseStream;
  }

  @Override
  public void disconnect() {}

  @Override
  public boolean usingProxy() {
    return false;
  }

  @Override
  public void connect() throws IOException {}

  @Override
  public int getResponseCode() throws IOException {
    return 200; // Simulate a successful response
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return responseStream;
  }
}
