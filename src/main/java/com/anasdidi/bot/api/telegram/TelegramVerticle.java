package com.anasdidi.bot.api.telegram;

import com.anasdidi.bot.common.AppConstants;
import com.anasdidi.bot.common.AppUtils;
import com.anasdidi.bot.common.TelegramVO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.ext.web.client.WebClient;

public class TelegramVerticle extends AbstractVerticle {

  private static final Logger logger = LogManager.getLogger(TelegramVerticle.class);
  private final EventBus eventBus;
  private final WebClient webClient;

  public TelegramVerticle(EventBus eventBus, WebClient webClient) {
    this.eventBus = eventBus;
    this.webClient = webClient;
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    String tag = "start";

    eventBus.consumer(AppConstants.TelegramMethod.SendMessage.value).handler(this::sendMessage);

    logger.info("[{}} {} started", tag, TelegramVerticle.class.getSimpleName());
    startPromise.complete();
  }

  void sendMessage(Message<Object> request) {
    String tag = AppConstants.TelegramMethod.SendMessage.value;
    JsonObject requestBody = new JsonObject((String) request.body());
    String requestId = requestBody.getString("requestId");

    TelegramVO vo = new TelegramVO(requestBody);
    String telegramUrl = AppUtils.getTelegramUrl(AppConstants.TelegramMethod.SendMessage);
    String responseBody = requestBody.getString("response");
    JsonObject message = AppUtils.getTelegramSendMessageBody(vo.getMessageFromId(), responseBody);

    webClient.postAbs(telegramUrl)//
        .putHeader(AppConstants.Header.ContentType.value, AppConstants.MediaType.AppJson.value)//
        .rxSendJsonObject(message).subscribe(response -> {
          logger.info("[{}:{}] Sent successfully", tag, requestId);
        }, e -> {
          logger.error("[{}:{}] Sent failed!", tag, requestId);
          logger.error(e);
        });
  }
}
