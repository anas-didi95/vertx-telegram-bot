package com.anasdidi.bot.api.expense;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;

public class ExpenseVerticle extends AbstractVerticle {

  private static final Logger logger = LogManager.getLogger(ExpenseVerticle.class);
  private final EventBus eventBus;

  public ExpenseVerticle(EventBus eventBus) {
    this.eventBus = eventBus;
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    final String TAG = "start";

    eventBus.consumer("/expense").handler(request -> {
      JsonObject responseBody = new JsonObject()//
          .put("message", "Budget saved");

      request.reply(responseBody);
    });

    logger.info("[{}] {} started.", TAG, ExpenseVerticle.class.getSimpleName());
    startPromise.complete();
  }
}
