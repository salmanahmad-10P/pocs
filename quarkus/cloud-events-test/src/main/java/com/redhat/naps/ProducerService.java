package com.redhat.naps;

import java.net.URI;
import java.time.Duration;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.ce.OutgoingCloudEventMetadata;

@ApplicationScoped
public class ProducerService {

    private static final Logger log = Logger.getLogger(ProducerService.class);
    
    @Outgoing("cloud-event-outgoing")
    public Multi<Message> toCloudEvents() {

        String testString = "RHT";
        return Multi.createFrom().ticks().every(Duration.ofSeconds(5))
            .onOverflow().drop()
            .map(tick -> Message.of(testString).addMetadata(OutgoingCloudEventMetadata.builder()
                .withType("greetings")
                .withSource(URI.create("http://example.com"))
                .withSubject("greeting-message") .build()));
    }


}
