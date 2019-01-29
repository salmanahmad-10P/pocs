import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import io.vertx.ext.asyncsql.PostgreSQLClient;
import io.vertx.ext.asyncsql.AsyncSQLClient;

import java.util.function.Consumer;

// execute: /opt/jboss/vert.x-3.0.0-milestone6/bin/vertx run DatabaseServer.java
// vertx-mysql-postgresql-client documentation:   file:///u01/jboss/community/vertx/vertx-mysql-postgresql-client/target/docs/vertx-mysql-postgresql-client/java/index.html
/*
 * * @author <a href="http://tfox.org">Tim Fox</a>
 * */
public class DatabaseServer extends AbstractVerticle {

    public void start() throws Exception {
        String host = "docker1.ose.opentlc.com";
        int port = 5432;
        String username = "jdg";
        String password = "jdg";
        String database = "jdgcachestore";
        
        JsonObject config = new JsonObject()
                                    .put("host", host)
                                    .put("port", port)
                                    .put("username", username)
                                    .put("password", password)
                                    .put("database", database);
        AsyncSQLClient asyncSqlClient = PostgreSQLClient.createNonShared(vertx, config);
        vertx.createHttpServer().requestHandler(req -> {
            asyncSqlClient.getConnection(onSuccess(conn -> {
                conn.query("SELEC 1 AS something", onSuccess(resultSet -> {
                    System.out.println("someTest() result = "+ resultSet.getResults().get(0));
                }));
            }));
            req.response().putHeader("content-type", "text/html").end("<html><body><h1>Hello from vert.x!</h1></body></html>");
        }).listen(8080);
    }

    private <T> Handler<AsyncResult<T>> onSuccess(Consumer<T> consumer) {
        return result -> {
            if (result.failed()) {
                result.cause().printStackTrace();
            } else {
                consumer.accept(result.result());
            }
        };
    }
}
