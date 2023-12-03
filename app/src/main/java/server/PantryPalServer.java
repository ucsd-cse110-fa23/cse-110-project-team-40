package server;

import com.sun.net.httpserver.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.concurrent.*;

/**
 * Main class for PantryPal's server.
 *
 * <p>In charge of setting up APIs and data structures
 */
public class PantryPalServer {
  private static final int SERVER_PORT = 8100;
  private static final String SERVER_HOSTNAME = "localhost";

  public static void main(String[] args) throws IOException {
    ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    // create a map to store data
    AccountData accountData = new AccountDB(new MongoJSONDB("accounts", "username"));
    RecipeCreator creator = new ChatGPTBot();

    // create a server
    HttpServer server = HttpServer.create(new InetSocketAddress(SERVER_HOSTNAME, SERVER_PORT), 0);

    HashMap<String, WhisperSubject> perUserWhisperSubject = new HashMap<>();
    HashMap<String, ImageManager> perUserImageManager = new HashMap<>();
    // setup APIS
    HttpContext WhisperContext =
        server.createContext(
            "/whisper",
            new UserHandler(new WhisperAPIFactory(perUserWhisperSubject, new WhisperBot())));

    HttpContext recipeListUsersContext =
        server.createContext("/recipes", new UserHandler(new RecipeListAPIFactory()));
    HttpContext imageContext =
        server.createContext("/image", new UserHandler(new ImageAPIFactory(perUserImageManager)));

    HttpContext detailedRecipeContext =
        server.createContext("/recipe", new UserHandler(new DetailedRecipeAPIFactory()));
    HttpContext ShareContext =
        server.createContext("/share", new UserHandler(new ShareAPIFactory(perUserImageManager)));

    HttpContext authorizationContext =
        server.createContext("/authorization", new AuthorizationAPI(accountData));

    HttpContext accountContext = server.createContext("/account", new AccountAPI(accountData));

    HttpContext newRecipeContext =
        server.createContext(
            "/newrecipe", new UserHandler(new NewRecipeAPIFactory(creator, perUserWhisperSubject)));

    /* start server */
    server.setExecutor(threadPoolExecutor);
    server.start();
  }
}
