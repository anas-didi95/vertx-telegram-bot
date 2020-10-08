package com.anasdidi.bot.api.greet;

import com.anasdidi.bot.common.AppConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.Promise;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.ext.web.client.WebClient;

public class GreetVerticle extends AbstractVerticle {

  private static final Logger logger = LogManager.getLogger(GreetVerticle.class);
  private final EventBus eventBus;
  private final GreetController greetingController;

  public GreetVerticle(EventBus eventBus, WebClient webClient) {
    this.eventBus = eventBus;
    this.greetingController = new GreetController(webClient);
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    String tag = "start";

    eventBus.consumer(AppConstants.Event.Greeting.value, greetingController::eventSendHelloUser);

    logger.info("[{}] GreetingVerticle started.", tag);
    startPromise.complete();
  }
}
