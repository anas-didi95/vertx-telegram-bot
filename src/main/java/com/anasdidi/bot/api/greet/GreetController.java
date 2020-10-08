package com.anasdidi.bot.api.greet;

import com.anasdidi.bot.common.AppConfig;
import com.anasdidi.bot.common.AppConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.ext.web.client.WebClient;

public class GreetController {

  private static final Logger logger = LogManager.getLogger(GreetController.class);
  private final WebClient webClient;

  GreetController(WebClient webClient) {
    this.webClient = webClient;
  }

  void eventSendHelloUser(Message<Object> request) {
    String tag = AppConstants.Event.Greeting.value;
    JsonObject requestBody = new JsonObject((String) request.body());
    String requestId = requestBody.getString("requestId");

    AppConfig appConfig = AppConfig.instance();
    String requestURI = String.format("https://api.telegram.org/bot%s/sendMessage", appConfig.getTelegramToken());
    JsonObject responseBody = new JsonObject()//
        .put("chat_id", requestBody.getJsonObject("message").getJsonObject("from").getInteger("id"))//
        .put("text", "Hello, " + requestBody.getJsonObject("message").getJsonObject("from").getString("first_name"));
    webClient.postAbs(requestURI)//
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
