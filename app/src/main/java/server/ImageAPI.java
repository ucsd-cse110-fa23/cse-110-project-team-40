package server;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.json.JSONObject;

class ImageAPIFactory implements HttpUserAPIFactory {
  Map<String, ImageManager> perUserImageManager;

  ImageAPIFactory(Map<String, ImageManager> perUserImageManager) {
    this.perUserImageManager = perUserImageManager;
  }

  public HttpAPI makeAPI(String username) {
    ImageManager manager = perUserImageManager.get(username);
    if (manager == null) {
      manager = new ImageManager(new DalleBot(username));
      perUserImageManager.put(username, manager);
    }
    return new ImageAPI(manager);
  }
}

class ImageAPI extends HttpAPI {
  private ImageManager manager;

  ImageAPI(ImageManager manager) {
    this.manager = manager;
  }

  String handleGet(String query, String request) throws IOException {
    query = query.substring(query.indexOf("?") + 1);
    JSONObject json = new JSONObject();
    json.put("image", manager.getImageAsBase64(query));
    return json.toString();
  }
}
