package com.anasdidi.bot.api.telegram;

import com.anasdidi.bot.common.AppConfig;
import com.anasdidi.bot.common.AppConstants;

import io.vertx.core.json.JsonObject;

class TelegramUtils {

  static String getBotUrl(AppConstants.TelegramMethod method) {
    String urlTemplate = AppConstants.Telegram.UrlTemplate.value;
    String token = AppConfig.instance().getTelegramToken();

    return String.format(urlTemplate, token, method.value);
  }

  static JsonObject setupSendMessageBody(int chatId, String text) {
    return new JsonObject()//
        .put("chat_id", chatId)//
        .put("text", text);
  }

}
