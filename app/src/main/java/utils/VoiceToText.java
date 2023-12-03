package utils;

/** Interface for voice-to-text functionality. */
public interface VoiceToText {
  public void startRecording();

  public void stopRecording();

  public String getTranscript();
}
