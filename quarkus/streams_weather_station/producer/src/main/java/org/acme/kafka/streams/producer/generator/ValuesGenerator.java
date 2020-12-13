package org.acme.kafka.streams.producer.generator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.enterprise.context.ApplicationScoped;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ValuesGenerator {

    private static final Logger LOG = Logger.getLogger(ValuesGenerator.class);

    private Random random = new Random();

    private List<WeatherStation> stations = Collections.unmodifiableList(
        Arrays.asList(
                new WeatherStation(1, "Hamburg", 13),
                new WeatherStation(2, "Snowdonia", 5),
                new WeatherStation(3, "Boston", 11),
                new WeatherStation(4, "Tokio", 16),
                new WeatherStation(5, "Cusco", 12),
                new WeatherStation(6, "Svalbard", -7),
                new WeatherStation(7, "Porthsmouth", 11),
                new WeatherStation(8, "Oslo", 7),
                new WeatherStation(9, "Marrakesh", 20)
        ));
    
    @Outgoing("temperature-values")                                        
    public Multi<Record<Integer, String>> generateTemperature() {
        return Multi.createFrom().ticks().every(Duration.ofMillis(500))
            .onOverflow().drop()
            .map(tick -> {
                WeatherStation station = stations.get(random.nextInt(stations.size()));
                double temperature = BigDecimal.valueOf(random.nextGaussian() * 15 + station.averageTemperature)
                                        .setScale(1, RoundingMode.HALF_UP)
                                        .doubleValue();
                
                LOG.infov("stationId: {0}, station: {1}, temperature: {2}", station.id, station.name, temperature);
                return Record.of(Integer.valueOf(station.id), Instant.now()+";"+temperature);
            });
    }

    @Outgoing("weather-stations")
    public Multi<Record<Integer, String>> weatherStations() {
        return Multi.createFrom().items(stations.stream()
            .map(s -> Record.of(
                s.id, "{\"id\":"+s.id+",\"name\":\""+s.name+"\"}"))
        );
    }
    
    
    private static class WeatherStation {

        int id;
        String name;
        int averageTemperature;
    
        public WeatherStation(int id, String name, int averageTemperature) {
            this.id = id;
            this.name = name;
            this.averageTemperature = averageTemperature;
        }
    }

    
}
