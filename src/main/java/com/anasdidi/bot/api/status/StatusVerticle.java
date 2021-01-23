package com.anasdidi.bot.api.status;

import com.anasdidi.bot.common.AppConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.reactivex.Single;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;

public class StatusVerticle extends AbstractVerticle {

  private final static Logger logger = LogManager.getLogger(StatusVerticle.class);
  private final EventBus eventBus;
  private final StatusController statusController;

  public StatusVerticle(EventBus eventBus, WebClient webClient) {
    this.eventBus = eventBus;
    statusController = new StatusController(webClient, eventBus);
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    final String TAG = "start";

    eventBus.consumer("/status").handler(this::eventGetStatus);

    logger.info("[{}] {} started.", TAG, StatusVerticle.class.getSimpleName());
    startPromise.complete();
  }

  void eventGetStatus(Message<Object> request) {
    final String TAG = "eventGetStatus2";
    JsonObject requestBody = new JsonObject((String) request.body());
    String requestId = requestBody.getString("requestId");

    ServiceDiscovery serviceDiscovery = ServiceDiscovery.create(vertx);
    Single<Boolean> securityService = serviceDiscovery
        .rxGetRecord(new JsonObject().put("name", "service-http-security")).isEmpty();
    Single<Boolean> botService = serviceDiscovery.rxGetRecord(new JsonObject().put("name", "service-http-bot"))
        .isEmpty();

    Single.zip(securityService, botService, (securityNotAvailable, botNotAvailable) -> {
      serviceDiscovery.close();

      if (logger.isDebugEnabled()) {
        logger.debug("[{}:{}] securityNotAvailable={}, botNotAvailable={}", TAG, requestId, securityNotAvailable,
            botNotAvailable);
      }

      return new JsonObject()//
          .put("security", !botNotAvailable)//
          .put("bot", !botNotAvailable)//
          .put("budget", true);
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
