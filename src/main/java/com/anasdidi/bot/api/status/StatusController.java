package com.anasdidi.bot.api.status;

import com.anasdidi.bot.common.AppConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;

class StatusController {

  private static final Logger logger = LogManager.getLogger(StatusController.class);
  private final WebClient webClient;

  StatusController(WebClient webClient) {
    this.webClient = webClient;
  }

  void eventGetStatus(Message<Object> request) {
    String tag = AppConstants.Event.Status.value;
    JsonObject requestBody = new JsonObject((String) request.body());
    String requestId = requestBody.getString("requestId");

    Single<HttpResponse<Buffer>> securityPing = webClient.getAbs(StatusConstants.ServerPing.Security.value).rxSend();
    Single<HttpResponse<Buffer>> botPing = webClient.getAbs(StatusConstants.ServerPing.Bot.value).rxSend();

    Single.zip(securityPing, botPing, (security, bot) -> {
      JsonObject securityBody = security.bodyAsJsonObject();
      JsonObject botBody = bot.bodyAsJsonObject();

      if (logger.isDebugEnabled()) {
        logger.debug("[{}:{}] securityBody\n{}", tag, requestId, securityBody.encodePrettily());
        logger.debug("[{}:{}] botBody\n{}", tag, requestId, botBody.encodePrettily());
      }

      return new JsonObject()//
          .put("security", securityBody.getString("outcome"))//
          .put("bot", botBody.getString("outcome"));
    }).subscribe(responseBody -> {
      request.reply(responseBody.encode());
    });
  }

}
