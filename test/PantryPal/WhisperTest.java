package PantryPal;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class WhisperTest {
    WhisperBot whisper;
    private ByteArrayOutputStream outputStream;

    @Before
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

        String expectedOutput = "--boundary\r\n" +
                                "Content-Disposition: form-data; name=\"paramName\"\r\n\r\n" +
                                "paramValue\r\n";

        String actualOutput = new String(outputStream.toByteArray());

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void testWriteFileToOutputStream() throws IOException {
        String boundary = "boundary";
        File testFile = new File("test/PantryPal/testfile.txt"); // Change this to the path of a test file on your system

        WhisperBot.writeFileToOutputStream(outputStream, testFile, boundary);
        outputStream.close();

        String expectedOutputStart = "--boundary\r\n" +
                                     "Content-Disposition: form-data; name=\"file\"; filename=\"testfile.txt\"\r\n" +
                                     "Content-Type: audio/mpeg\r\n\r\n";

        // Verify the file content
        FileInputStream fileInputStream = new FileInputStream(testFile);
        byte[] fileContent = new byte[(int) testFile.length()];
        fileInputStream.read(fileContent);
        fileInputStream.close();

        byte[] actualOutput = outputStream.toByteArray();

        // Compare the expected start of the output with the actual output
        assertArrayEquals(expectedOutputStart.getBytes(), actualOutput);

        // Compare the file content with the actual output starting from the end of the expected output
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
        WhisperBot childWhisperer = new WhisperBot();
        childWhisperer.handleSuccessResponse(mockConnection);

        // Check if the 'output' field is set correctly
        assertEquals("Hello", childWhisperer.output);
    }

    // A simple mock for HttpURLConnection to provide a predefined response stream
    private static class MockHttpURLConnection extends HttpURLConnection {
        private final ByteArrayInputStream responseStream;

        protected MockHttpURLConnection(ByteArrayInputStream responseStream) {
            super(null);
            this.responseStream = responseStream;
        }

        @Override
        public void disconnect() {
        }

        @Override
        public boolean usingProxy() {
            return false;
        }

        @Override
        public void connect() throws IOException {
        }

        @Override
        public int getResponseCode() throws IOException {
            return 200; // Simulate a successful response
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return responseStream;
        }
    }
}

