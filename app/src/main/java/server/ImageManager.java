package server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;

public class ImageManager {
  private HashMap<String, String> paths;
  private ImageGenerator generator;

  ImageManager(ImageGenerator generator) {
    this.paths = new HashMap<String, String>();
    this.generator = generator;
  }

  public void addImage(String title) {
    paths.put(title, generator.generateImage(title));
  }

  /*
   * @require hasUser(username)
   */
  public boolean hasImage(String title) {
    return paths.containsKey(title);
  }

  public String getImageAsPath(String title) {
    if (!hasImage(title)) {
      addImage(title);
    }
    return paths.get(title);
  }

  public InputStream getImageAsStream(String title) throws IOException {
    String path = getImageAsPath(title);
    File file = new File(path);
    return new FileInputStream(file);
  }

  public String getImageAsBase64(String title) throws IOException {
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    InputStream in = getImageAsStream(title);
    in.transferTo(byteStream);
    return Base64.getEncoder().encodeToString(byteStream.toByteArray());
  }
}
