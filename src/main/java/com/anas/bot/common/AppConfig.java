package com.anas.bot.common;

import io.vertx.core.json.JsonObject;

public class AppConfig {

  private static AppConfig appConfig;
  private JsonObject config;

  private AppConfig(JsonObject config) {
    this.config = config;
  }

  @Override
  public String toString() {
    return new JsonObject()//
        .put("APP_HOST", appConfig.getAppHost())//
        .put("APP_PORT", appConfig.getAppPort())//
        .put("TELEGRAM_TOKEN", appConfig.getTelegramToken())//
        .encodePrettily();
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

  public String getAppHost() {
    return config.getString("APP_HOST", "localhost");
  }

  public int getAppPort() {
    return config.getInteger("APP_PORT", 5000);
  }

  public String getTelegramToken() {
    return config.getString("TELEGRAM_TOKEN");
  }
}
