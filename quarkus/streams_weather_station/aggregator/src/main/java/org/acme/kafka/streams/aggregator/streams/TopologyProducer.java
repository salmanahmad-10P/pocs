package org.acme.kafka.streams.aggregator.streams;

import java.time.Instant;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.acme.kafka.streams.aggregator.model.Aggregation;
import org.acme.kafka.streams.aggregator.model.TemperatureMeasurement;
import org.acme.kafka.streams.aggregator.model.WeatherStation;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;

import io.quarkus.kafka.client.serialization.JsonbSerde;

@ApplicationScoped
public class TopologyProducer {

    static final String WEATHER_STATIONS_STORE = "weather-stations-store";

    private static final String WEATHER_STATIONS_TOPIC = "weather-stations";
    private static final String TEMPERATURE_VALUES_TOPIC = "temperature-values";
    private static final String TEMPERATURES_AGGREGATED_TOPIC = "temperatures-aggregated";

    /*
      By specifying a CDI producer method which returns the Kafka Streams Topology, the Quarkus extension will take care of configuring, starting and stopping the actual Kafka Streams engine
    */
    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        JsonbSerde<WeatherStation> weatherStationSerde = new JsonbSerde<>(WeatherStation.class);
        JsonbSerde<Aggregation> aggregationSerde = new JsonbSerde<>(Aggregation.class);

        KeyValueBytesStoreSupplier storeSupplier = Stores.persistentKeyValueStore(WEATHER_STATIONS_STORE);

        GlobalKTable<Integer, WeatherStation> stationsTable = builder.globalTable( 
                WEATHER_STATIONS_TOPIC,
                Consumed.with(Serdes.Integer(), weatherStationSerde));

        builder.stream(                                                       
                        TEMPERATURE_VALUES_TOPIC,
                        Consumed.with(Serdes.Integer(), Serdes.String())
                )
                .join(                                                        
                        stationsTable,
                        (stationIdFromStreamedTemp, timestampAndValueFromStreamedTemp) -> stationIdFromStreamedTemp,
                        (timestampAndValueFromStreamedTemp, stationFromKTable) -> {
                            String[] parts = timestampAndValueFromStreamedTemp.split(";");
                            return new TemperatureMeasurement(stationFromKTable.id, stationFromKTable.name, Instant.parse(parts[0]), Double.valueOf(parts[1]));
                        }
                )
                .groupByKey()                                                 
                .aggregate(                                                   
                        Aggregation::new,
                        (stationId, value, aggregation) -> aggregation.updateFrom(value),
                        Materialized.<Integer, Aggregation> as(storeSupplier)
                            .withKeySerde(Serdes.Integer())
                            .withValueSerde(aggregationSerde)
                )
                .toStream()
                .to(                                                          
                        TEMPERATURES_AGGREGATED_TOPIC,
                        Produced.with(Serdes.Integer(), aggregationSerde)
                );

        return builder.build();
    }

}
