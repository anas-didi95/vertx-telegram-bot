package com.anasdidi.bot.domain.greeting;

import com.anasdidi.bot.common.AppConfig;
import com.anasdidi.bot.common.AppConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.ext.web.client.WebClient;

public class GreetingVerticle extends AbstractVerticle {

  private static final Logger logger = LogManager.getLogger(GreetingVerticle.class);
  private final EventBus eventBus;

  public GreetingVerticle(EventBus eventBus) {
    this.eventBus = eventBus;
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    WebClient webClient = WebClient.create(vertx);
    AppConfig appConfig = AppConfig.instance();

    eventBus.consumer(AppConstants.Event.Greeting.value, request -> {
      String tag = AppConstants.Event.Greeting.value;
      JsonObject requestBody = new JsonObject((String) request.body());
      String requestId = requestBody.getString("requestId");

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
    });
  }
}
