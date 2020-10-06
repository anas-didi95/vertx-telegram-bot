package com.anasdidi.bot.common;

public class AppConstants {

  public enum Event {
    Greeting("/getMe");

    public String value;

    private Event(String value) {
      this.value = value;
    }
  }

  public enum Header {
    ContentType("Content-Type");

    public String value;

    private Header(String value) {
      this.value = value;
    }
  }

  public enum MediaType {
    AppJson("application/json");

    public String value;

    private MediaType(String value) {
      this.value = value;
    }
  }
}
