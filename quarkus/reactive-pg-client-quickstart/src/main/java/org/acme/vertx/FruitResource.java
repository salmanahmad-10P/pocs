package org.acme.vertx;

import java.util.concurrent.CompletionStage;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;




@Path("fruits")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FruitResource {

    @Inject
    io.vertx.axle.pgclient.PgPool client;

    @Inject
    @ConfigProperty(name = "myapp.schema.create", defaultValue = "true")
    boolean schemaCreate;

    @PostConstruct
    void config() {
        if (schemaCreate) {
            initdb();
        }
    }

    @GET
    public CompletionStage<Response> get() {
        return Fruit.findAll(client).thenApply(Response::ok).thenApply(ResponseBuilder::build);
    }

    @POST
    public CompletionStage<Response> post() {
        return Fruit.addFruit();
    }

    private void initdb() {
        client.query("DROP TABLE IF EXISTS fruits")
                .thenCompose(r -> client.query("CREATE TABLE fruits (id SERIAL PRIMARY KEY, name TEXT NOT NULL)"))
                .thenCompose(r -> client.query("INSERT INTO fruits (name) VALUES ('Orange')"))
                .thenCompose(r -> client.query("INSERT INTO fruits (name) VALUES ('Pear')"))
                .thenCompose(r -> client.query("INSERT INTO fruits (name) VALUES ('Apple')"))
                .toCompletableFuture()
                .join();
    }
}
