package com.anasdidi.bot.api.status;

import com.anasdidi.bot.MainVerticle;

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
  void testCommandStatusSuccess(Vertx vertx, VertxTestContext testContext) {
    JsonObject requestBody = new JsonObject()//
        .put("message", new JsonObject()//
            .put("text", "/status"));

    vertx.eventBus().rxRequest("/status", requestBody.encode()).subscribe(response -> {
      testContext.verify(() -> {
        JsonObject responseBody = new JsonObject((String) response.body());
        Assertions.assertNotNull(responseBody);

        String security = responseBody.getString("security");
        Assertions.assertEquals("UP", security);

        String bot = responseBody.getString("bot");
        Assertions.assertEquals("UP", bot);

        testContext.completeNow();
      });
    }, e -> testContext.failNow(e));
  }
}
