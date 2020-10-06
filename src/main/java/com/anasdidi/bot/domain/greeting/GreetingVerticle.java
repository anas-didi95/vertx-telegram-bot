package com.anasdidi.bot.domain.greeting;

import com.anasdidi.bot.common.AppConstant;

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
    eventBus.consumer(AppConstant.Event.Greeting.value, handler -> {
      JsonObject request = new JsonObject((String) handler.body());
      JsonObject response = new JsonObject()
          .put("status", new JsonObject().put("isSuccess", true).put("message", "Greeting received."))
          .put("data", new JsonObject().put("value", "Hello"));
      String message = response.encode();

      if (logger.isDebugEnabled()) {
        logger.debug("[start] event={}, request\n{}", "get-greeting", request.encodePrettily());
        logger.debug("[start] event={}, response\n{}", "get-greeting", response.encodePrettily());
      }

      logger.info("[start] event={}, message={}", "get-greeting", message);
      handler.reply(message);
    });
  }
}
