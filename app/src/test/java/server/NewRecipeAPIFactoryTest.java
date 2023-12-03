package server;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class NewRecipeAPIFactoryTest {

  @Test
  public void testMakeAPI() {
    Map<String, WhisperSubject> perUserWhisperSubject = new HashMap<String, WhisperSubject>();

    NewRecipeAPIFactory newRecipeAPIFactory =
        new NewRecipeAPIFactory(new RecipeCreatorStub(), perUserWhisperSubject);

    WhisperSubject existingUserData = new WhisperSubject();

    /* ensure makeAPI creats a new whispersubject if does not exist */
    newRecipeAPIFactory.makeAPI("newUser");
    assertEquals(true, perUserWhisperSubject.containsKey("newUser"));
    assertEquals(1, perUserWhisperSubject.get("newUser").countObservers());

    /* ensure makeAPI does not write over existing WhisperSubject */
    perUserWhisperSubject.put("existingUser", existingUserData);
    newRecipeAPIFactory.makeAPI("existingUser");
    assertEquals(existingUserData, perUserWhisperSubject.get("existingUser"));
    assertEquals(1, perUserWhisperSubject.get("existingUser").countObservers());
  }
}
