package com.anas.bot.common;

import io.vertx.core.json.JsonObject;

public class AppConfig {

  private static AppConfig appConfig;
  private JsonObject config;

  private AppConfig(JsonObject config) {
    this.config = config;
  }

  public static AppConfig create(JsonObject config) {
    appConfig = new AppConfig(config);
    return appConfig;
  }

  public static AppConfig instance() throws Exception {
    if (appConfig == null) {
      throw new Exception("AppConfig is null!");
    }
    return appConfig;
  }

  public String getTelegramToken() {
    return config.getString("TELEGRAM_TOKEN");
  }
}
