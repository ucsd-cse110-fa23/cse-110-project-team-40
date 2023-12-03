package PantryPal;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import utils.Account;

public class ShareLinkMakerTest {
  private ShareLinkMaker linkMaker = new ShareLinkMaker();

  @Test
  public void testShareLinkMaker() {
    String title = "Fluffy Pancakes with Poached Eggs";
    Account user = new Account("account3", "password");
    String expectedLink = "http://localhost:8100/share/account3/Fluffy-Pancakes-with-Poached-Eggs";
    assertEquals(expectedLink, linkMaker.makeLink(title, user));
  }
}
