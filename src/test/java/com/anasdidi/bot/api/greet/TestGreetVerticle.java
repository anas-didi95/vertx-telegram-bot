package com.anasdidi.bot.api.greet;

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
public class TestGreetVerticle {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void testGetGreetSuccess(Vertx vertx, VertxTestContext testContext) throws Exception {
    int testValueInt = (int) (System.currentTimeMillis() / 1000);
    String testValueStr = "" + testValueInt;
    JsonObject requestBody = new JsonObject()//
        .put("message", new JsonObject()//
            .put("text", AppConstants.Event.Greet.value)//
            .put("from", new JsonObject()//
                .put("first_name", "first_name:" + testValueStr)//
                .put("id", testValueInt)));

    vertx.eventBus().rxRequest(AppConstants.Event.Greet.value, requestBody.encode()).subscribe(response -> {
      StringBuilder expectedMessage = new StringBuilder()//
          .append("Hello, first_name:" + testValueStr).append("\n")//
          .append("\n")//
          .append("Telegram Id:").append("\n")//
          .append(testValueInt);

      testContext.verify(() -> {
        JsonObject responseBody = new JsonObject((String) response.body());
        Assertions.assertNotNull(responseBody);

        String message = responseBody.getString("response");
        Assertions.assertEquals(expectedMessage.toString(), message);

        testContext.completeNow();
      });
    }, e -> testContext.failNow(e));
  }
}
