package com.anasdidi.bot.api.greet;

import com.anasdidi.bot.common.AppConstants;
import com.anasdidi.bot.common.AppUtils;
import com.anasdidi.bot.common.TelegramVO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.ext.web.client.WebClient;

class GreetController {

  private static final Logger logger = LogManager.getLogger(GreetController.class);
  private final WebClient webClient;

  GreetController(WebClient webClient) {
    this.webClient = webClient;
  }

  void eventSendHelloUser(Message<Object> request) {
    String tag = AppConstants.Event.Greet.value;
    JsonObject requestBody = new JsonObject((String) request.body());
    String requestId = requestBody.getString("requestId");

    TelegramVO vo = new TelegramVO(requestBody);
    String telegramUrl = AppUtils.getTelegramUrl(AppConstants.TelegramMethod.SendMessage);
    JsonObject responseBody = AppUtils.getTelegramSendMessageBody(vo.getMessageFromId(),
        "Hello, " + vo.getMessageFromFirstname());
    webClient.postAbs(telegramUrl)//
        .putHeader(AppConstants.Header.ContentType.value, AppConstants.MediaType.AppJson.value)//
        .rxSendJsonObject(responseBody).subscribe(response -> {
          logger.info("[{}:{}] Sent successfully", tag, requestId);
        }, e -> {
          logger.error("[{}:{}] Sent failed!", tag, requestId);
          logger.error(e);
        });

    request.reply(responseBody.encode());
  }
}
