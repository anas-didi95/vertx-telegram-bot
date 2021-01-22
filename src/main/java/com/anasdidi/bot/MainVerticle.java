package com.anasdidi.bot;

import java.util.HashSet;
import java.util.Set;

import com.anasdidi.bot.api.greet.GreetVerticle;
import com.anasdidi.bot.api.status.StatusVerticle;
import com.anasdidi.bot.api.telegram.TelegramVerticle;
import com.anasdidi.bot.common.AppConfig;
import com.anasdidi.bot.common.AppConstants;
import com.anasdidi.bot.common.AppUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.Status;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.ext.healthchecks.HealthCheckHandler;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.CorsHandler;

public class MainVerticle extends AbstractVerticle {

  private static final Logger logger = LogManager.getLogger(MainVerticle.class);
  private static final long serverStartTime = System.currentTimeMillis();
  private EventBus eventBus;
  private WebClient webClient;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    final String TAG = "[start]";
    ConfigRetriever configRetriever = ConfigRetriever.create(vertx,
        new ConfigRetrieverOptions().addStore(new ConfigStoreOptions().setType("env")));

    configRetriever.rxGetConfig().subscribe(config -> {
      AppConfig appConfig = AppConfig.create(config);
      logger.info("{} appConfig\n{}", TAG, appConfig.toString());

      HealthCheckHandler healthCheckHandler = HealthCheckHandler.create(vertx);
      healthCheckHandler.register("server-uptime", procedure -> {
        long duration = System.currentTimeMillis() - serverStartTime;
        procedure.complete(Status.OK(new JsonObject()//
            .put("startTime", serverStartTime + "ms")//
            .put("uptime", duration + "ms")//
            .put("formatted", AppUtils.getFormattedServerUptime(duration))));
      });

      Router router = Router.router(vertx);
      router.route().handler(setupCorsHandler());
      router.route().handler(BodyHandler.create());
      router.route().handler(routingContext -> routingContext.put("requestId", AppUtils.generateId()).next());
      router.post("/").handler(this::requestHandler);
      router.get("/ping").handler(healthCheckHandler);
      router.get("/test")
          .handler(routingContext -> routingContext.response().end(new JsonObject().put("ok", true).encode()));

      this.eventBus = vertx.eventBus();
      this.webClient = WebClient.create(vertx);
      vertx.deployVerticle(new GreetVerticle(eventBus));
      vertx.deployVerticle(new StatusVerticle(eventBus, webClient));
      vertx.deployVerticle(new TelegramVerticle(eventBus, webClient));

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
    final String TAG = "requestHandler";
    String requestId = routingContext.get("requestId");
    JsonObject requestBody = routingContext.getBodyAsJson()//
        .put("requestId", requestId);

    if (logger.isDebugEnabled()) {
      logger.debug("[{}:{}] requestBody\n{}", TAG, requestId, requestBody.encodePrettily());
    }

    String messageText = requestBody.getJsonObject("message").getString("text");
    String event = messageText.split(" ")[0];

    boolean hasFound = false;
    for (AppConstants.Event e : AppConstants.Event.values()) {
      hasFound = hasFound || e.value.equals(event);
    }

    if (!hasFound) {
      logger.error("[{}:{}] Event is undefined! event={}", TAG, requestId, event);
      routingContext.response().end();
    } else {
      eventBus.rxRequest(event, requestBody.encode()).subscribe(response -> {
        if (logger.isDebugEnabled()) {
          JsonObject responseBody = new JsonObject((String) response.body());
          logger.debug("[{}:{}] responseBody\n{}", TAG, requestId, responseBody.encodePrettily());
        }
        logger.info("[{}:{}] Event success, event={}", TAG, requestId, event);
      }, e -> {
        logger.error("[{}:{}] Event failed! event={}", TAG, requestId, event);
        logger.error(e);
      });

      routingContext.response().end();
    }
  }

  CorsHandler setupCorsHandler() {
    Set<String> headerNames = new HashSet<>();
    headerNames.add("Accept");
    headerNames.add("Content-Type");

    Set<HttpMethod> methods = new HashSet<>();
    methods.add(HttpMethod.GET);
    methods.add(HttpMethod.POST);

    return CorsHandler.create("*")//
        .allowedHeaders(headerNames)//
        .allowedMethods(methods);
  }
}
