package com.anasdidi.bot.api.expense;

import com.anasdidi.bot.common.AppConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.Promise;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;

public class ExpenseVerticle extends AbstractVerticle {

  private static final Logger logger = LogManager.getLogger(ExpenseVerticle.class);
  private final EventBus eventBus;
  private final ExpenseController expenseController;

  public ExpenseVerticle(EventBus eventBus) {
    this.eventBus = eventBus;
    this.expenseController = new ExpenseController();
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    final String TAG = "start";

    eventBus.consumer(AppConstants.Event.Expense.value).handler(expenseController::eventSaveBudget);

    logger.info("[{}] {} started.", TAG, ExpenseVerticle.class.getSimpleName());
    startPromise.complete();
  }
}
