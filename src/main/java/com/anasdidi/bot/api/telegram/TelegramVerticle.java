package com.anasdidi.bot.api.telegram;

import com.anasdidi.bot.common.AppConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.Promise;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.ext.web.client.WebClient;

public class TelegramVerticle extends AbstractVerticle {

  private static final Logger logger = LogManager.getLogger(TelegramVerticle.class);
  private final EventBus eventBus;
  private final TelegramController telegramController;

  public TelegramVerticle(EventBus eventBus, WebClient webClient) {
    this.eventBus = eventBus;
    this.telegramController = new TelegramController(webClient);
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    String tag = "start";

    eventBus.consumer(AppConstants.TelegramMethod.SendMessage.value).handler(telegramController::eventSendMessage);

    logger.info("[{}] {} started", tag, TelegramVerticle.class.getSimpleName());
    startPromise.complete();
  }

}
