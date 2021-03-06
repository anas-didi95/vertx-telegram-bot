package com.anasdidi.bot.api.status;

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
public class TestStatusVerticle {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void testGetStatusSuccess(Vertx vertx, VertxTestContext testContext) {
    JsonObject requestBody = new JsonObject()//
        .put("message", new JsonObject()//
            .put("text", AppConstants.Event.Status.value)//
            .put("from", new JsonObject()//
                .put("id", 000)));

    vertx.eventBus().rxRequest(AppConstants.Event.Status.value, requestBody.encode()).subscribe(response -> {
      testContext.verify(() -> {
        JsonObject responseBody = new JsonObject((String) response.body());
        Assertions.assertNotNull(responseBody);

        String messageExpected = new StringBuilder()//
            .append("Server status\n")//
            .append("\n")//
            .append("security: ").append(AppConstants.Emoji.Tick.value).append("\n")//
            .append("bot: ").append(AppConstants.Emoji.Tick.value)//
            .toString();
        String message = responseBody.getString("response");
        Assertions.assertEquals(messageExpected, message);

        testContext.completeNow();
      });
    }, e -> testContext.failNow(e));
  }
}
