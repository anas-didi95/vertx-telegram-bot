package com.anasdidi.bot.domain.greeting;

import com.anasdidi.bot.MainVerticle;
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
import io.vertx.reactivex.ext.web.client.predicate.ResponsePredicate;

@ExtendWith(VertxExtension.class)
public class TestGreetingVerticle {

  private String requestURI = "/bot";

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void testGetGreetingSuccess(Vertx vertx, VertxTestContext testContext) throws Exception {
    AppConfig appConfig = AppConfig.instance();
    WebClient webClient = WebClient.create(vertx);
    JsonObject body = new JsonObject()//
        .put("message", new JsonObject()//
            .put("text", "greeting")//
            .put("from", new JsonObject()//
                .put("id", System.currentTimeMillis())));

    webClient.post(appConfig.getAppPort(), appConfig.getAppHost(), requestURI)//
        .expect(ResponsePredicate.SC_OK).expect(ResponsePredicate.JSON).rxSendJsonObject(body).subscribe(response -> {
          testContext.verify(() -> {
            JsonObject responseBody = response.bodyAsJsonObject();
            Assertions.assertNotNull(responseBody);

            JsonObject status = responseBody.getJsonObject("status");
            Assertions.assertNotNull(status);
            Assertions.assertEquals(true, status.getBoolean("isSuccess"));
            Assertions.assertEquals("Greeting received.", status.getString("message"));

            JsonObject data = responseBody.getJsonObject("data");
            Assertions.assertNotNull(data);
            Assertions.assertEquals("Hello", data.getString("value"));

            testContext.completeNow();
          });
        }, e -> testContext.failNow(e));
  }
}
