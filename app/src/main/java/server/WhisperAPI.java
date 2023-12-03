package server;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.util.*;

interface ServerVoiceToText {
  String getTranscript(InputStream voiceData);
}

class WhisperSubject extends Observable {
  @Override
  public void addObserver(Observer o) {
    System.err.println("adding observer");
    super.addObserver(o);
  }

  void set(String data) {
    System.err.println("notifying observers");
    setChanged();
    notifyObservers(data);
  }
}

class WhisperAPIFactory implements HttpUserAPIFactory {
  Map<String, WhisperSubject> perUserWhisperSubject;
  ServerVoiceToText serverVoiceToText;

  WhisperAPIFactory(
      Map<String, WhisperSubject> perUserWhisperSubject, ServerVoiceToText serverVoiceToText) {
    this.serverVoiceToText = serverVoiceToText;
    this.perUserWhisperSubject = perUserWhisperSubject;
  }

  public RawHttpAPI makeAPI(String username) {
    WhisperSubject whisperSubject = perUserWhisperSubject.get(username);
    if (whisperSubject == null) {
      whisperSubject = new WhisperSubject();
      perUserWhisperSubject.put(username, whisperSubject);
    }
    System.err.println("WhisperFactory subject: " + whisperSubject.toString());
    return new WhisperAPI(whisperSubject, serverVoiceToText);
  }
}

class WhisperAPI extends RawHttpAPI {
  ServerVoiceToText serverVoiceToText;
  WhisperSubject whisperSubject;

  WhisperAPI(WhisperSubject whisperSubject, ServerVoiceToText serverVoiceToText) {
    this.serverVoiceToText = serverVoiceToText;
    this.whisperSubject = whisperSubject;
  }

  String handlePut(String query, InputStream body) throws IOException {
    String transcript = serverVoiceToText.getTranscript(body);
    whisperSubject.set(transcript);
    return transcript;
  }
}
