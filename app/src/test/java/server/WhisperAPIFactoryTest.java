package server;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class WhisperAPIFactoryTest {

  @Test
  public void testMakeAPI() {
    Map<String, WhisperSubject> perUserWhisperSubject = new HashMap<String, WhisperSubject>();

    WhisperAPIFactory whisperAPIFactory =
        new WhisperAPIFactory(perUserWhisperSubject, new MockServerVoiceToText());

    WhisperSubject existingUserData = new WhisperSubject();

    /* ensure makeAPI creats a new whispersubject if does not exist */
    WhisperAPI whisperAPI = (WhisperAPI) whisperAPIFactory.makeAPI("newUser");
    assertEquals(true, perUserWhisperSubject.containsKey("newUser"));

    /* ensure makeAPI does not write over existing WhisperSubject */
    perUserWhisperSubject.put("existingUser", existingUserData);
    whisperAPIFactory.makeAPI("existingUser");
    assertEquals(existingUserData, perUserWhisperSubject.get("existingUser"));
  }
}
