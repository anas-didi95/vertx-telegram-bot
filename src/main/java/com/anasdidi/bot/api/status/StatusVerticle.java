package com.anasdidi.bot.api.status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.Promise;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.ext.web.client.WebClient;

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
    String tag = "start";

    eventBus.consumer("/status").handler(statusController::eventGetStatus);

    logger.info("[{}] {} started.", tag, StatusVerticle.class.getSimpleName());
    startPromise.complete();
  }
}
