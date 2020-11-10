package com.anasdidi.bot.api.expense;

import com.anasdidi.bot.common.TelegramVO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;

class ExpenseController {

  private static final Logger logger = LogManager.getLogger(ExpenseController.class);

  void eventSaveBudget(Message<Object> request) {
    final String TAG = "eventSaveBudget";
    JsonObject requestBody = (JsonObject) request.body();
    String requestId = requestBody.getString("requestId");
    TelegramVO vo = new TelegramVO(requestBody);
    String text = vo.getMessageText();

    if (logger.isDebugEnabled()) {
      logger.debug("[{}:{}] text={}", TAG, requestId, text);
    }

    String item = text.substring(text.indexOf(" ") + 1, text.lastIndexOf(" "));
    String priceStr = text.substring(text.lastIndexOf(" ") + 1);

    if (logger.isDebugEnabled()) {
      logger.debug("[{}:{}] item={}, priceStr={}", TAG, requestId, item, priceStr);
    }

    String message = new StringBuilder()//
        .append("Budget saved").append("\n")//
        .append("- Item: ").append(item).append("\n")//
        .append("- Price: ").append(priceStr).toString();
    JsonObject responseBody = new JsonObject()//
        .put("message", message);

    request.reply(responseBody);
  }
}
