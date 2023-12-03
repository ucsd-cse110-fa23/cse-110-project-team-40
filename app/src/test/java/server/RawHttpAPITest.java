package server;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import org.junit.jupiter.api.Test;

public class RawHttpAPITest {
  RawHttpAPI dut = new RawHttpAPI();
  public static final String exceptionMessage = "Request type not supported";

  @Test
  public void testPut() {
    boolean exceptionHappened = false;
    try {
      dut.handlePut("", (InputStream) null);
    } catch (IOException e) {
      exceptionHappened = true;
      assertEquals(exceptionMessage, e.getMessage());
    }
    assertEquals(true, exceptionHappened);
  }

  @Test
  public void testDelete() {
    boolean exceptionHappened = false;
    try {
      dut.handleDelete("", (InputStream) null);
    } catch (IOException e) {
      exceptionHappened = true;
      assertEquals(exceptionMessage, e.getMessage());
    }
    assertEquals(true, exceptionHappened);
  }

  @Test
  public void testPost() {
    boolean exceptionHappened = false;
    try {
      dut.handlePost("", (InputStream) null);
    } catch (IOException e) {
      exceptionHappened = true;
      assertEquals(exceptionMessage, e.getMessage());
    }
    assertEquals(true, exceptionHappened);
  }

  @Test
  public void testGet() {
    boolean exceptionHappened = false;
    try {
      dut.handleGet("", (InputStream) null);
    } catch (IOException e) {
      exceptionHappened = true;
      assertEquals(exceptionMessage, e.getMessage());
    }
    assertEquals(true, exceptionHappened);
  }
}
