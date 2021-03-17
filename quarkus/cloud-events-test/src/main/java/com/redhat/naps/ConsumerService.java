package com.redhat.naps;

import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import io.smallrye.reactive.messaging.ce.IncomingCloudEventMetadata;

@ApplicationScoped
public class ConsumerService {

    private static final Logger log = Logger.getLogger(ConsumerService.class);

    @Incoming("cloud-event-incoming")
    public CompletionStage<Void> process(Message<String> cloudEvent) {
        IncomingCloudEventMetadata<String> cloudEventMetadata = cloudEvent.getMetadata(IncomingCloudEventMetadata.class).orElseThrow(() -> new IllegalArgumentException("Expected a Cloud Event"));

        log.infof("Received Cloud Events (spec-version: %s): id: '%s', source:  '%s', type: '%s', subject: '%s' , message: '%s'",
            cloudEventMetadata.getSpecVersion(),
            cloudEventMetadata.getId(),
            cloudEventMetadata.getSource(),
            cloudEventMetadata.getType(),
            cloudEventMetadata.getSubject().orElse("no subject"),
            cloudEvent.getPayload());
        
        return cloudEvent.ack();
    }

}
