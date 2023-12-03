/* Code originally adapted from lab5 */
package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

/** Class responsible for recording audio using the Java Sound API. */
public class AudioRecorder {
  private AudioFormat audioFormat;
  private TargetDataLine targetDataLine;
  private boolean isRecording;
  public static final String filePath = "output.wav";

  /** Constructor to set up the audio format. */
  public AudioRecorder() {
    audioFormat = new AudioFormat(44100, 16, 1, true, false);
  }

  public boolean isRecording() {
    return isRecording;
  }

  /**
   * get InputStream for audio file created by recorder
   *
   * @return Input stream for audio file, null if I/O err
   */
  public InputStream getAudioInStream() {
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

  public void start() {
    if (isRecording) {
      return;
    }

    try {
      // Get the TargetDataLine from the AudioSystem
      DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
      targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
      targetDataLine.open(audioFormat);
      targetDataLine.start();

      isRecording = true;

      // Start a new thread to handle the recording process
      new Thread(
              () -> {
                try {
                  AudioInputStream audioInputStream = new AudioInputStream(targetDataLine);
                  AudioSystem.write(
                      audioInputStream, AudioFileFormat.Type.WAVE, new java.io.File(filePath));
                } catch (Exception e) {
                  e.printStackTrace();
                }
              })
          .start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /** Stop recording audio. */
  public void stop() {
    if (!isRecording) {
      return;
    }

    try {
      targetDataLine.stop();
      targetDataLine.close();
      isRecording = false;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
