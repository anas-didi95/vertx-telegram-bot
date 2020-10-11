package com.anasdidi.bot.api.status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.reactivex.Single;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;

public class StatusVerticle extends AbstractVerticle {

  private final static Logger logger = LogManager.getLogger(StatusVerticle.class);
  private final EventBus eventBus;
  private final WebClient webClient;

  public StatusVerticle(EventBus eventBus, WebClient webClient) {
    this.eventBus = eventBus;
    this.webClient = webClient;
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    String tag = "start";

    eventBus.consumer("/status").handler(this::handler);

    logger.info("[{}] {} started.", StatusVerticle.class.getSimpleName(), tag);
    startPromise.complete();
  }

  public void handler(Message<Object> request) {
    String tag = "/status";
    JsonObject requestBody = new JsonObject((String) request.body());
    String requestId = requestBody.getString("requestId");

    Single<HttpResponse<Buffer>> securityPing = webClient.getAbs("https://api.anasdidi.dev/security/ping").rxSend();
    Single<HttpResponse<Buffer>> botPing = webClient.getAbs("https://api.anasdidi.dev/bot/ping").rxSend();

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
