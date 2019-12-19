package org.acme.vertx;

import io.vertx.axle.pgclient.PgPool;
import io.vertx.axle.sqlclient.Row;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;



public class Fruit {
    public Long id;

    public String name;

    public Fruit() {
    }

    public Fruit(String name) {
        this.name = name;
    }

    public Fruit(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static CompletionStage<List<Fruit>> findAll(PgPool client) {
        return client.query("SELECT id, name FROM fruits ORDER BY name ASC").thenApply(pgRowSet -> {
            List<Fruit> list = new ArrayList<>(pgRowSet.size());
            for (Row row : pgRowSet) {
                list.add(from(row));
            }
            return list;
        });
    }

    private static Fruit from(Row row) {
        return new Fruit(row.getLong("id"), row.getString("name"));
    }

	public static CompletionStage<String> addFruit(PgPool client, String name) {
        Fruit newFruit = new Fruit(name);
		return null;
	}

}
