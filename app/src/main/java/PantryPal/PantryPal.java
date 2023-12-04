/* Code initially adapted from Lab1 */

package PantryPal;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.event.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import utils.Account;
import utils.Recipe;

interface HomeTracker {
  public ScrollablePage getHome();

  public ScrollablePage getError();
}

class AppController implements HomeTracker {
  private Account account;
  private PageTracker pt;

  public AppController(PageTracker pt) {
    this.pt = pt;
  }

  public ScrollablePage getError() {
    return makeErrorPage();
  }

  public ScrollablePage getHome() {

    if (account == null) {
      return makeLoginPage();
    } else {
      return makeRecipeListPage();
    }
  }

  public boolean makeAccount(AccountCreateUI accountCreateUI, AccountModel accountModel) {
    Account account = accountCreateUI.getAccount();
    if (!Account.isValidUsername(account.getUsername())) {
      accountCreateUI.setErrorText("Please enter a valid username");
      return false;
    }
    if (!accountModel.create(account)) {
      accountCreateUI.setErrorText("Username already exists");
      return false;
    }
    return true;
  }

  public boolean validateAccount(
      AccountLoginUI accountLoginUI, AuthorizationModel authorizationModel) {
    Account account = accountLoginUI.getAccount();
    if (!authorizationModel.tryConnect()) {
      ServerErrorPage serverErrorPage = makeErrorPage();
      pt.swapToPage(serverErrorPage);
      return false;
    }

    if (!Account.isValidUsername(account.getUsername())) {
      accountLoginUI.setErrorText("Please enter a valid username");
      return false;
    }
    if (!authorizationModel.authenticate(account)) {
      accountLoginUI.setErrorText("Invalid username/password");
      return false;
    }
    return true;
  }

  public ServerErrorPage makeErrorPage() {
    ServerErrorUI serverErrorUI = new ServerErrorUI();
    ServerErrorPage serverErrorPage = new ServerErrorPage(serverErrorUI);

    serverErrorPage.footer.addButton(
        "Refresh",
        e -> {
          if (serverErrorUI.tryConnect()) {
            System.err.println("Server is on");
            pt.goHome();
          } else {
            System.err.println("Server still down");
            pt.goError();
          }
        });

    return serverErrorPage;
  }

  public AccountCreatePage makeAccountCreatePage() {
    AccountCreateUI accountCreateUI = new AccountCreateUI();
    AccountCreatePage accountCreatePage = new AccountCreatePage(accountCreateUI);
    accountCreatePage.footer.addButton(
        "Create Account",
        e -> {
          HttpRequestModel httpModel = new HttpRequestModel();
          httpModel.registerObserver(pt);
          boolean madeAccount = makeAccount(accountCreateUI, new AccountModel(httpModel));
          if (madeAccount) {
            pt.swapToPage(makeLoginPage());
          }
        });

    return accountCreatePage;
  }

  public AccountLoginPage makeLoginPage() {
    AccountLoginUI accountLoginUI = new AccountLoginUI();
    AccountLoginPage accountLoginPage = new AccountLoginPage(accountLoginUI);
    // TODO check if autologin exists and if so autoswap to home page

    accountLoginPage.footer.addButton(
        "Login",
        e -> {
          Account account =
              new Account(accountLoginUI.getUserNameText(), accountLoginUI.getPasswordText());
          HttpRequestModel httpModel = new HttpRequestModel();
          httpModel.registerObserver(pt);
          boolean loggedIn = validateAccount(accountLoginUI, new AuthorizationModel(httpModel));

          if (loggedIn) {
            this.account = accountLoginUI.getAccount();
            // TODO check if loginValid && autoLogin selected and if so enable autologin US11
            pt.swapToPage(makeRecipeListPage()); // Swap to recipe list page
          }
        });

    accountLoginPage.footer.addButton(
        "Create Account",
        e -> {
          pt.swapToPage(makeAccountCreatePage());
        });
    return accountLoginPage;
  }

  public RecipeListPage makeRecipeListPage() {
    RecipeListPage recipeList = new RecipeListPage(getRecipeListEntries());
    recipeList.footer.addButton(
        "New Recipe",
        e -> {
          pt.swapToPage(makeNewRecipeController().getPage());
        });
    return recipeList;
  }

  public List<RecipeEntryUI> getRecipeListEntries() {
    HttpRequestModel httpModel = new HttpRequestModel();
    httpModel.registerObserver(pt);
    RecipeListModel model = new RecipeListModel(httpModel, account);
    ArrayList<RecipeEntryUI> entries = new ArrayList<>();
    ArrayList<Recipe> recipes = new ArrayList<>();

    for (Recipe recipe : model.getRecipeList()) {
      entries.add(makeRecipeEntryUI(recipe));
    }

    return entries;
  }

  public RecipeEntryUI makeRecipeEntryUI(Recipe recipe) {
    RecipeEntryUI entry = new RecipeEntryUI(recipe);
    entry.addButton(
        "details",
        e -> {
          pt.swapToPage(makeRecipeDetailsPage(recipe.getTitle()));
        });
    return entry;
  }

  public RecipeDetailPage makeRecipeDetailsPage(String title) {
    HttpRequestModel httpModel = new HttpRequestModel();
    httpModel.registerObserver(pt);
    RecipeDetailModel rc = new RecipeDetailModel(httpModel, account);
    RecipeDetailPage drp = new RecipeDetailPage(new RecipeDetailUI(rc.read(title), rc));
    drp.footer.addButton(
        "home",
        e -> {
          pt.swapToPage(makeRecipeListPage());
        });
    return drp;
  }

  public NewRecipeController makeNewRecipeController() {
    NewRecipePage newRecipePage = new NewRecipePage(new NewRecipeUI());
    HttpRequestModel httpModel = new HttpRequestModel();
    httpModel.registerObserver(pt);
    NewRecipeModel newRecipeModel = new NewRecipeModel(httpModel, account);
    VoiceToText voiceToText = new WhisperBot();
    return new NewRecipeController(newRecipePage, newRecipeModel, pt, voiceToText, account);
  }
}

interface ServerObserver {
  void updateServer(boolean connected);
}

/*
 * Object which handles which Page is currently displayed
 */
class PageTracker implements ServerObserver {
  private Stage primaryStage;
  private HomeTracker homeTracker;

  PageTracker(Stage primaryStage) {
    // Set the title of the app
    primaryStage.setTitle("PantryPal");
    this.primaryStage = primaryStage;
  }

  void setHomeTracker(HomeTracker tracker) {
    this.homeTracker = tracker;
  }

  @Override
  public void updateServer(boolean connected) {
    if (connected) this.goHome();
    else this.goError();
  }

  /**
   * "Swaps" to displaying a new page
   *
   * @param page page to display
   */
  void swapToPage(ScrollablePage page) {
    primaryStage.setScene(page.getWrapperScene());
    primaryStage.setResizable(false);
    primaryStage.show();
  }

  void goHome() {
    swapToPage(homeTracker.getHome());
  }

  void goError() {
    swapToPage(homeTracker.getError());
  }
}

/** Runner class which starts the UI */
public class PantryPal extends Application {
  RecipeListPage recipelist;
  PageTracker pageTracker;

  @Override
  public void start(Stage primaryStage) throws Exception {
    PageTracker pt = new PageTracker(primaryStage);
    AppController appController = new AppController(pt);
    pt.setHomeTracker(appController);
    pt.goHome();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
