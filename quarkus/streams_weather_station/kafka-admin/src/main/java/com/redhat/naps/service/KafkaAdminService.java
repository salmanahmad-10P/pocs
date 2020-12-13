package com.redhat.naps.service;


import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.AlterConfigsResult;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.DescribeConfigsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.clients.admin.AlterConfigOp;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Context;
import io.vertx.mutiny.core.Future;
import io.vertx.mutiny.core.Vertx;

@ApplicationScoped
public class KafkaAdminService {

  private Logger log = Logger.getLogger("KafkaAdminService");

  @Inject
  Vertx vertx;
  
  @ConfigProperty(name="KAFKA_PROVIDER_HOST_PORT", defaultValue ="localhost:9092")
  private String kafkaProviderHostPort;

  public void listKafkaConfigs() throws ExecutionException, InterruptedException {
      Properties config = new Properties();
      config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProviderHostPort);
      AdminClient admin = AdminClient.create(config);
      StringBuilder sBuilder = new StringBuilder();
      for (Node node : admin.describeCluster().nodes().get()) {
          sBuilder.append("\n-- node: " + node.id() + " --");
          ConfigResource cr = new ConfigResource(ConfigResource.Type.BROKER, "0");
          DescribeConfigsResult dcr = admin.describeConfigs(Collections.singleton(cr));
          dcr.all().get().forEach((k, c) -> {
              c.entries()
               .forEach(configEntry -> {sBuilder.append("\n\t\t"+configEntry.name() + "= " + configEntry.value());});
          });
      }
      log.infov("Kafka configs from {0} = {1}", this.kafkaProviderHostPort, sBuilder.toString());
  }

  public Uni<String> updateTopicConfig(String topicName, String key, String value) {
    Properties props = new Properties();
    props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProviderHostPort);
    AdminClient admin = AdminClient.create(props);

    Uni<String> returnUni = Uni.createFrom().item(key);

    final ConfigResource configResource = new ConfigResource(ConfigResource.Type.TOPIC, topicName);
    final List<ConfigEntry> configEntries = new ArrayList<>();
    configEntries.add(new ConfigEntry(key, value));
    final Config config = new Config(configEntries);
    AlterConfigsResult acResults = admin.alterConfigs(Collections.singletonMap(configResource, config));
    acResults.all().whenComplete((v, ex) -> {
        if(ex != null){
            ex.printStackTrace();
            returnUni.onItem().transform(n -> String.format("Exception thrown updating key {0} , {1}", key, ex.getMessage()));
        }else {
            returnUni.onItem().transform(n -> String.format("Success updating key {0}", key));
        }
    });
    return returnUni;
  }

  public void createTopic(String topicName) {
    Properties config = new Properties();
    config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProviderHostPort);
    AdminClient admin = AdminClient.create(config);
    log.infov("creating topic {0}"+topicName);
    NewTopic newTopic = new NewTopic(topicName, 1, (short) 1);
    admin.createTopics(Collections.singleton(newTopic));
  }
}

