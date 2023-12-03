package server;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DalleTest {

  private MockDalleBot dalle;
  private final String mockResponseJson = "{\"data\":[{\"url\":\"http://example.com/image.jpg\"}]}";
  private final String expectedImagePath = "out/PantryPal/recipeImages/mockImage.jpg";

  @BeforeEach
  public void setUp() {
    dalle = new MockDalleBot();
    MockHttpURLConnection mockConnection =
        new MockHttpURLConnection(new ByteArrayInputStream(mockResponseJson.getBytes()));
  }

  @Test
  public void testGenerateImage() throws IOException {
    String imagePath = dalle.generateImage("mockImage");
    assertEquals(expectedImagePath, imagePath);
    assertFalse(Files.exists(Paths.get(imagePath))); // the mock is not actually creating it
  }

  private static class MockHttpURLConnection extends HttpURLConnection {

    public MockHttpURLConnection(ByteArrayInputStream mockInput) {
      super(null);
    }

    @Override
    public void disconnect() {
      // Mock disconnect method
    }

    @Override
    public boolean usingProxy() {
      // assumed not  connection is not using a proxy
      return false;
    }

    @Override
    public void connect() throws IOException {
      // Mock connect method
    }

    @Override
    public int getResponseCode() throws IOException {
      return HttpURLConnection.HTTP_OK; // Assume successful response
    }

    @Override
    public InputStream getInputStream() throws IOException {
      // Simulated JSON response with 'data' key
      String mockResponseJson = "{\"data\":[{\"url\":\"http://example.com/image.jpg\"}]}";
      ByteArrayInputStream mockInput = new ByteArrayInputStream(mockResponseJson.getBytes());
      return mockInput;
    }

    // If you need to test error scenarios, you can override getErrorStream as well
    @Override
    public InputStream getErrorStream() {
      // simulating error response
      return null;
    }
  }
}
