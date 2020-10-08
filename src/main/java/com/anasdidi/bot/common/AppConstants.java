package com.anasdidi.bot.common;

public class AppConstants {

  public enum Event {
    Greet("/greet");

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

  public enum Telegram {
    UrlTemplate("https://api.telegram.org/bot%s/%s");

    public String value;

    private Telegram(String value) {
      this.value = value;
    }
  }

  public enum TelegramMethod {
    SendMessage("sendMessage");

    public String value;

    private TelegramMethod(String value) {
      this.value = value;
    }
  }
}
