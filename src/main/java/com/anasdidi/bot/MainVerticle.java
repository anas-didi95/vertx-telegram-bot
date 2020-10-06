package com.anasdidi.bot;

import com.anasdidi.bot.common.AppConfig;
import com.anasdidi.bot.common.AppConstants;
import com.anasdidi.bot.common.AppUtils;
import com.anasdidi.bot.domain.greeting.GreetingVerticle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;

public class MainVerticle extends AbstractVerticle {

  private static final Logger logger = LogManager.getLogger(MainVerticle.class);
  private EventBus eventBus;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    final String TAG = "[start]";
    ConfigRetriever configRetriever = ConfigRetriever.create(vertx,
        new ConfigRetrieverOptions().addStore(new ConfigStoreOptions().setType("env")));

    configRetriever.rxGetConfig().subscribe(config -> {
      AppConfig appConfig = AppConfig.create(config);
      logger.info("{} appConfig\n{}", TAG, appConfig.toString());

      Router router = Router.router(vertx);
      router.route().handler(BodyHandler.create());
      router.route().handler(routingContext -> routingContext.put("requestId", AppUtils.generateId()).next());
      router.post("/").handler(this::requestHandler);

      this.eventBus = vertx.eventBus();
      vertx.deployVerticle(new GreetingVerticle(eventBus));

      Router contextPath = Router.router(vertx).mountSubRouter("/bot", router);
      int port = appConfig.getAppPort();
      String host = appConfig.getAppHost();
      vertx.createHttpServer().requestHandler(contextPath).rxListen(port, host).subscribe(server -> {
        logger.info("HTTP server started on {}:{}", appConfig.getAppHost(), appConfig.getAppPort());
        startPromise.complete();
      }, e -> startPromise.fail(e));
    }, e -> startPromise.fail(e));
  }

  private void requestHandler(RoutingContext routingContext) {
    String requestId = routingContext.get("requestId");
    String tag = "requestHandler:" + requestId;
    JsonObject requestBody = routingContext.getBodyAsJson()//
        .put("requestId", requestId);

    if (logger.isDebugEnabled()) {
      logger.debug("[{}] requestBody\n{}", tag, requestBody.encodePrettily());
    }

    String event = requestBody.getJsonObject("message").getString("text");
    eventBus.rxRequest(event, requestBody.encode()).subscribe(handler -> {
      JsonObject response = new JsonObject((String) handler.body());
      routingContext.response()//
          .setStatusCode(200)//
          .putHeader(AppConstants.Header.ContentType.value, AppConstants.MediaType.AppJson.value)//
          .end(response.encode());
    }, e -> {
      logger.error("[{}] onError : e={}", tag, e);
      JsonObject response = new JsonObject()//
          .put("status", new JsonObject()//
              .put("isSuccess", false)//
              .put("message", "Get greeting failed!"))//
          .put("error", e.getMessage());
      routingContext.response()//
          .setStatusCode(400)//
          .putHeader(AppConstants.Header.ContentType.value, AppConstants.MediaType.AppJson.value)//
          .end(response.encode());
    });
  }
}
