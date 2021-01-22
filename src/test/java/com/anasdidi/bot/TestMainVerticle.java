package com.anasdidi.bot;

import com.anasdidi.bot.common.AppConfig;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.client.WebClient;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) throws Throwable {
    testContext.completeNow();
  }

  @Test
  void testAppConfigSuccess(Vertx vertx, VertxTestContext testContext) {
    AppConfig appConfig = AppConfig.instance();
    testContext.verify(() -> {
      Assertions.assertNotNull(appConfig);
      Assertions.assertNotNull(appConfig.getAppHost());
      Assertions.assertNotNull(appConfig.getAppPort());
      Assertions.assertNotNull(appConfig.getLogLevel());
      Assertions.assertNotNull(appConfig.getTelegramToken());

      testContext.completeNow();
    });
  }

  @Test
  void testPingSuccess(Vertx vertx, VertxTestContext testContext) {
    AppConfig appConfig = AppConfig.instance();
    WebClient webClient = WebClient.create(vertx);

    webClient.get(appConfig.getAppPort(), appConfig.getAppHost(), "/bot/ping").rxSend().subscribe(response -> {
      testContext.verify(() -> {
        Assertions.assertEquals(200, response.statusCode());

        JsonObject responseBody = response.bodyAsJsonObject();
        Assertions.assertNotNull(responseBody);
        Assertions.assertEquals("UP", responseBody.getString("outcome"));

        testContext.completeNow();
      });
    }, e -> testContext.failNow(e));
  }
}
