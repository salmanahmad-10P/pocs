package com.redhat.naps;

import io.quarkus.runtime.StartupEvent;

import java.util.concurrent.ExecutionException;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.redhat.naps.service.KafkaAdminService;



@ApplicationScoped
public class KafkaAdminEntry {

    @Inject
    private KafkaAdminService kAdminService;

    public void onStart(@Observes @Priority(value = 1) StartupEvent ev) throws ExecutionException, InterruptedException {
        kAdminService.listKafkaConfigs();

    }
    
}
