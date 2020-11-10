package com.anasdidi.bot.api.expense;

import com.anasdidi.bot.MainVerticle;
import com.anasdidi.bot.common.AppConstants;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;

@ExtendWith(VertxExtension.class)
public class TestExpenseVerticle {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void testSaveStatusSuccess(Vertx vertx, VertxTestContext testContext) {
    JsonObject requestBody = new JsonObject()//
        .put("message", new JsonObject()//
            .put("text", AppConstants.Event.Expense.value)//
            .put("from", new JsonObject()//
                .put("id", 000)));

    vertx.eventBus().rxRequest(AppConstants.Event.Expense.value, requestBody).subscribe(response -> {
      testContext.verify(() -> {
        JsonObject responseBody = (JsonObject) response.body();
        Assertions.assertNotNull(responseBody);

        String expectedMessage = new StringBuilder()//
            .append("Budget saved")//
            .toString();
        Assertions.assertEquals(expectedMessage, responseBody.getString("message"));

        testContext.completeNow();
      });
    }, e -> testContext.failNow(e));

  }
}
