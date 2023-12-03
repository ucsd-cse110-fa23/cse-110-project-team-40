/* Code initially adapted from Lab1 */

package PantryPal;

import java.io.*;
import javafx.event.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import utils.Account;
import utils.AudioRecorder;
import utils.VoiceToText;

/** Communication model for sending audio to Whisper during new recipe creation. */
public class WhisperModel implements VoiceToText {
  private HttpModel httpModel;
  private Account account;
  private AudioRecorder audioRecorder;

  WhisperModel(HttpModel httpModel, Account account) {
    this.audioRecorder = new AudioRecorder();
    this.httpModel = httpModel;
    httpModel.setPath("whisper/" + account.getUsername() + "/");
  }

  public String getTranscript() {
    InputStream in = audioRecorder.getAudioInStream();
    String response = httpModel.performRawRequest("PUT", in);
    System.err.println("Whisper Model responded: " + response);
    return response;
  }

  public void startRecording() {
    audioRecorder.start();
  }

  public void stopRecording() {
    audioRecorder.stop();
  }
}
