package com.anasdidi.bot.api.status;

import com.anasdidi.bot.common.AppConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;

class StatusController {

  private static final Logger logger = LogManager.getLogger(StatusController.class);
  private final WebClient webClient;
  private final EventBus eventBus;

  StatusController(WebClient webClient, EventBus eventBus) {
    this.webClient = webClient;
    this.eventBus = eventBus;
  }

  void eventGetStatus(Message<Object> request) {
    final String TAG = AppConstants.Event.Status.value;
    JsonObject requestBody = new JsonObject((String) request.body());
    String requestId = requestBody.getString("requestId");

    Single<HttpResponse<Buffer>> securityPing = webClient.getAbs(StatusConstants.ServerPing.Security.value).rxSend();
    Single<HttpResponse<Buffer>> botPing = webClient.getAbs(StatusConstants.ServerPing.Bot.value).rxSend();
    Single<HttpResponse<Buffer>> budgetPing = webClient.getAbs(StatusConstants.ServerPing.Budget.value).rxSend();

    Single.zip(securityPing, botPing, budgetPing, (security, bot, budget) -> {
      JsonObject securityBody = security.bodyAsJsonObject();
      JsonObject botBody = bot.bodyAsJsonObject();
      JsonObject budgetBody = budget.bodyAsJsonObject();

      if (logger.isDebugEnabled()) {
        logger.debug("[{}:{}] securityBody\n{}", TAG, requestId, securityBody.encodePrettily());
        logger.debug("[{}:{}] botBody\n{}", TAG, requestId, botBody.encodePrettily());
        logger.debug("[{}:{}] budgetBody\n{}", TAG, requestId, budgetBody.encodePrettily());
      }

      return new JsonObject()//
          .put("security", securityBody.getString("outcome").equals("UP"))//
          .put("bot", botBody.getString("outcome").equals("UP"))//
          .put("budget", budgetBody.getString("outcome").equals("UP"));
    }).subscribe(result -> {
      String response = new StringBuilder()//
          .append("Server status\n")//
          .append("\n")//
          .append("security: ")
          .append(result.getBoolean("security") ? AppConstants.Emoji.Tick.value : AppConstants.Emoji.Cross.value)
          .append("\n")//
          .append("bot: ")
          .append(result.getBoolean("bot") ? AppConstants.Emoji.Tick.value : AppConstants.Emoji.Cross.value)//
          .append("\n")//
          .append("budget: ")
          .append(result.getBoolean("budget") ? AppConstants.Emoji.Tick.value : AppConstants.Emoji.Cross.value)//
          .toString();

      if (logger.isDebugEnabled()) {
        logger.debug("[{}:{}] response={}", TAG, requestId, response);
      }

      requestBody.put("response", response);
      eventBus.publish(AppConstants.TelegramMethod.SendMessage.value, requestBody.encode());

      request.reply(requestBody.encode());
    });
  }

}
