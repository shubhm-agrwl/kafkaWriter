package com.shubham.kafkawriter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;

public class KafkaWriterVerticle extends AbstractVerticle {

  public void start() throws InterruptedException {
    System.out.println("Check Vertex");

    vertx.createHttpServer(new HttpServerOptions().setPort(8080)).requestHandler(new Handler<HttpServerRequest>() {
      public void handle(HttpServerRequest req) {
        req.response().end("<body><h1>Hello World from Java!</h1></body>");
      }
    }).listen();

  }

}
