package com.anasdidi.bot.api.expense;

import com.anasdidi.bot.common.AppConstants;
import com.anasdidi.bot.common.TelegramVO;

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

    eventBus.consumer(AppConstants.Event.Expense.value).handler(request -> {
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
    });

    logger.info("[{}] {} started.", TAG, ExpenseVerticle.class.getSimpleName());
    startPromise.complete();
  }
}
