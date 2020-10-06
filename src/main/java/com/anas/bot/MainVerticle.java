package com.anas.bot;

import com.anas.bot.common.AppConfig;

import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Promise;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    ConfigRetriever configRetriever = ConfigRetriever.create(vertx,
        new ConfigRetrieverOptions().addStore(new ConfigStoreOptions().setType("env")));

    configRetriever.rxGetConfig().subscribe(config -> {
      AppConfig appConfig = AppConfig.create(config);
      System.out.println("appConfig\n" + appConfig.toString());

      vertx.createHttpServer().requestHandler(req -> {
        req.response().putHeader("content-type", "text/plain").end("Hello from Vert.x!");
      }).listen(5000, http -> {
        if (http.succeeded()) {
          startPromise.complete();
          System.out.println("HTTP server started on port 5000");
        } else {
          startPromise.fail(http.cause());
        }
      });
    }, e -> startPromise.fail(e));
  }
}
