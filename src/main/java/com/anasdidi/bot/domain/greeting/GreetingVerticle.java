package com.anasdidi.bot.domain.greeting;

import com.anasdidi.bot.common.AppConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;

public class GreetingVerticle extends AbstractVerticle {

  private static final Logger logger = LogManager.getLogger(GreetingVerticle.class);
  private final EventBus eventBus;

  public GreetingVerticle(EventBus eventBus) {
    this.eventBus = eventBus;
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    eventBus.consumer(AppConstants.Event.Greeting.value, handler -> {
      JsonObject request = new JsonObject((String) handler.body());
      String tag = AppConstants.Event.Greeting.value + ":" + request.getString("requestId");

      if (logger.isDebugEnabled()) {
        logger.debug("[{}] request\n{}", tag, request.encodePrettily());
      }

      JsonObject response = new JsonObject()//
          .put("status", new JsonObject()//
              .put("isSuccess", true)//
              .put("message", "Greeting received."))//
          .put("data", new JsonObject()//
              .put("value", "Hello"));

      if (logger.isDebugEnabled()) {
        logger.debug("[{}] response\n{}", tag, response.encodePrettily());
      }

      String message = response.encode();
      logger.info("[{}] message={}", tag, message);
      handler.reply(message);
    });
  }
}
