package com.anasdidi.bot.common;

import java.util.UUID;

import io.vertx.core.json.JsonObject;

public class AppUtils {

  public static String generateId() {
    return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
  }

  public static String getTelegramUrl(AppConstants.TelegramMethod method) {
    String urlTemplate = AppConstants.Telegram.UrlTemplate.value;
    String token = AppConfig.instance().getTelegramToken();

    return String.format(urlTemplate, token, method.value);
  }

  public static JsonObject getTelegramSendMessageBody(int chatId, String text) {
    return new JsonObject()//
        .put("chat_id", chatId)//
        .put("text", text);
  }
}
