package org.acme;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreetingVerticle extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(GreetingVerticle.class);
    private int port = 8081;

    public void start(io.vertx.core.Future<Void> future) throws Exception {
        HttpServerOptions hOptions = new HttpServerOptions();
        hOptions.setPort(port);
        HttpServer hServer = vertx.createHttpServer(hOptions);
        logger.info("start() just started httpserver =" + hServer);

        hServer.requestHandler(request -> {
            HttpServerResponse hResponse = request.response();
            hResponse.setStatusCode(200).end("Nice job!");
        });

    }

    public void stop(io.vertx.core.Future<Void> future) throws Exception {

    }

}