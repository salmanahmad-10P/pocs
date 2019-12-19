package org.acme;

// 1 of 3 API models provided by Vert.x
import io.vertx.axle.core.Vertx;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Path("/")
public class GreetingVerticle {

    private final Logger logger = LoggerFactory.getLogger(GreetingVerticle.class);
    private int delay = 10000; // in millis

    @Inject
    Vertx vertx;

    // This appears to get invoked the first time a request is placed
    // Only one instance of this class is instantiated .... so its a Singleton
    @PostConstruct
    void init() {
        logger.info("init() vertx = "+vertx);

        // vert.x loop executes this in the background as:  vert.x-eventloop-thread-0
        vertx.setPeriodic(delay, id -> {
            logger.info("tick");
          });
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{name}")
    public CompletionStage<String> greeting(@PathParam String name) {
        
        // When complete, return the content to the client
        CompletableFuture<String> future = new CompletableFuture<>();

        long start = System.nanoTime();

        // Delay
        vertx.setTimer(delay, l -> {
            // Compute elapsed time in milliseconds
            long duration = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);

            // Format message
            String message = String.format("Hello %s! (%d ms)%n", name, duration);
            logger.info("greeting() message = "+message);

            // Complete
            future.complete(message);
        });

        return future;
    }


}
