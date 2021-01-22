package com.anasdidi.bot.api.telegram;

import com.anasdidi.bot.common.AppConstants;
import com.anasdidi.bot.common.TelegramVO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.ext.web.client.WebClient;

class TelegramController {

  private static final Logger logger = LogManager.getLogger(TelegramController.class);
  private final WebClient webClient;

  TelegramController(WebClient webClient) {
    this.webClient = webClient;
  }

  void eventSendMessage(Message<Object> request) {
    final String TAG = AppConstants.TelegramMethod.SendMessage.value;
    JsonObject requestBody = new JsonObject((String) request.body());
    String requestId = requestBody.getString("requestId");

    TelegramVO vo = new TelegramVO(requestBody);
    String telegramUrl = TelegramUtils.getBotUrl(AppConstants.TelegramMethod.SendMessage);
    String responseBody = requestBody.getString("response");
    JsonObject message = TelegramUtils.setupSendMessageBody(vo.getMessageFromId(), responseBody);

    webClient.postAbs(telegramUrl)//
        .putHeader(AppConstants.Header.ContentType.value, AppConstants.MediaType.AppJson.value)//
        .rxSendJsonObject(message).subscribe(response -> {
          logger.info("[{}:{}] Sent successfully", TAG, requestId);
        }, e -> {
          logger.error("[{}:{}] Sent failed!", TAG, requestId);
          logger.error(e);
        });
  }
}
