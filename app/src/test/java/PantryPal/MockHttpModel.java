package PantryPal;

import java.io.InputStream;

/** Mock HttpModel for testing */
public class MockHttpModel implements HttpModel {
  public String mockResponse;
  public String method;
  public String query;
  public String request;
  public InputStream in;

  @Override
  public void registerObserver(ServerObserver observer) {
    // Mock
  }

  @Override
  public void removeObserver(ServerObserver observer) {
    // Mock
  }

  @Override
  public void notifyServerStatus(boolean connected) {
    // Mock
  }

  @Override
  public boolean tryConnect() {
    // Mock constuctor
    return false;
  }

  @Override
  public void setPath(String path) {
    // Mock constuctor
  }

  @Override
  public String performRequest(String method, String query, String request) {
    this.method = method;
    this.query = query;
    this.request = request;
    return this.mockResponse;
  }

  @Override
  public String performRawRequest(String method, InputStream in) {
    this.in = in;
    this.method = method;
    return this.mockResponse;
  }

  /**
   * Sets the response for calling performRequest
   *
   * @param mockResponse
   */
  public void setMockResponse(String mockResponse) {
    this.mockResponse = mockResponse;
  }
}
