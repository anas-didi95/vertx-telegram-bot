package com.anasdidi.bot;

import com.anasdidi.bot.common.AppConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Promise;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;

public class MainVerticle extends AbstractVerticle {

  private static final Logger logger = LogManager.getLogger(MainVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    ConfigRetriever configRetriever = ConfigRetriever.create(vertx,
        new ConfigRetrieverOptions().addStore(new ConfigStoreOptions().setType("env")));

    configRetriever.rxGetConfig().subscribe(config -> {
      AppConfig appConfig = AppConfig.create(config);
      logger.info("appConfig\n{}", appConfig.toString());

      vertx.createHttpServer().requestHandler(req -> {
        req.response().putHeader("content-type", "text/plain").end("Hello from Vert.x!");
      }).listen(appConfig.getAppPort(), appConfig.getAppHost(), http -> {
        if (http.succeeded()) {
          logger.info("HTTP server started on {}:{}", appConfig.getAppHost(), appConfig.getAppPort());
          startPromise.complete();
        } else {
          startPromise.fail(http.cause());
        }
      });
    }, e -> startPromise.fail(e));
  }
}
