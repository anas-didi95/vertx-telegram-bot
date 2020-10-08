package com.anasdidi.bot.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;

public class AppConfig {

  private final static Logger logger = LogManager.getLogger(AppConfig.class);
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
        .put("LOG_LEVEL", appConfig.getLogLevel())//
        .put("TELEGRAM_TOKEN", appConfig.getTelegramToken())//
        .encodePrettily();
  }

  public static AppConfig create(JsonObject config) {
    appConfig = new AppConfig(config);
    return appConfig;
  }

  public static AppConfig instance() {
    if (appConfig == null) {
      logger.error("AppConfig is null!");
    }
    return appConfig;
  }

  public String getAppHost() {
    return config.getString("APP_HOST", "localhost");
  }

  public int getAppPort() {
    return config.getInteger("APP_PORT", 5000);
  }

  public String getLogLevel() {
    return config.getString("LOG_LEVEL", "error");
  }

  public String getTelegramToken() {
    return config.getString("TELEGRAM_TOKEN");
  }
}
