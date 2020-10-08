package com.anasdidi.bot.common;

import java.util.UUID;

public class AppUtils {

  public static String generateId() {
    return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
  }

  public static String getTelegramUrl(AppConstants.TelegramMethod method) {
    String urlTemplate = AppConstants.Telegram.UrlTemplate.value;
    String token = AppConfig.instance().getTelegramToken();

    return String.format(urlTemplate, token, method.value);
  }
}
