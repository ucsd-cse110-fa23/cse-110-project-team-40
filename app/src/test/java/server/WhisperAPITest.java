package server;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import org.junit.jupiter.api.Test;

class MockWhisperSubject extends WhisperSubject {
  String data;

  void set(String data) {
    this.data = data;
  }
}

class MockServerVoiceToText implements ServerVoiceToText {
  public String transcript;
  public InputStream lastVoiceData;

  public String getTranscript(InputStream voiceData) {
    this.lastVoiceData = voiceData;
    return transcript;
  }
}

public class WhisperAPITest {

  @Test
  public void testPut() {
    ByteArrayInputStream voiceData = new ByteArrayInputStream("Sample voice data".getBytes());
    MockWhisperSubject whisperSubject = new MockWhisperSubject();
    MockServerVoiceToText voiceToText = new MockServerVoiceToText();
    WhisperAPI whisperAPI = new WhisperAPI(whisperSubject, voiceToText);

    voiceToText.transcript = "This is a recipe";
    try {
      assertEquals(voiceToText.transcript, whisperAPI.handlePut(null, voiceData));
    } catch (Exception e) {
      fail("handlePut should never throw exception");
    }
    assertEquals(voiceToText.transcript, whisperSubject.data);
    assertEquals(voiceData, voiceToText.lastVoiceData);
  }
}
