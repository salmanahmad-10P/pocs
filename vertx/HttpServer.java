import io.vertx.core.AbstractVerticle;

// execute: /opt/jboss/vert.x-3.0.0-milestone6/bin/vertx run HttpServer.java
/*
 * * @author <a href="http://tfox.org">Tim Fox</a>
 * */
public class HttpServer extends AbstractVerticle {

    public void start() throws Exception {
        vertx.createHttpServer().requestHandler(req -> {
            req.response().putHeader("content-type", "text/html").end("<html><body><h1>Hello from vert.x!</h1></body></html>");
        }).listen(8080);
    }
}
