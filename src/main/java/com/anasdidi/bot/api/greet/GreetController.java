package com.anasdidi.bot.api.greet;

import com.anasdidi.bot.common.AppConstants;
import com.anasdidi.bot.common.TelegramVO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.Message;

class GreetController {

  private static final Logger logger = LogManager.getLogger(GreetController.class);
  private final EventBus eventBus;

  GreetController(EventBus eventBus) {
    this.eventBus = eventBus;
  }

  void eventSendHelloUser(Message<Object> request) {
    final String TAG = AppConstants.Event.Greet.value;
    JsonObject requestBody = new JsonObject((String) request.body());
    String requestId = requestBody.getString("requestId");

    TelegramVO vo = new TelegramVO(requestBody);
    StringBuilder response = new StringBuilder()//
        .append("Hello, ").append(vo.getMessageFromFirstname()).append("\n")//
        .append("\n")//
        .append("Telegram Id:").append("\n")//
        .append(vo.getMessageFromId());

    if (logger.isDebugEnabled()) {
      logger.debug("[{}:{}] response={}", TAG, requestId, response.toString());
    }

    requestBody.put("response", response.toString());
    eventBus.publish(AppConstants.TelegramMethod.SendMessage.value, requestBody.encode());

    request.reply(requestBody.encode());
  }
}
