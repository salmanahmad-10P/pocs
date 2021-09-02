1. Clone, build and deploy pneumonia-patient-processing-kjar
   `````
   $ git clone https://github.com/redhat-naps-da/pneumonia-patient-processing-kjar.git
   $ cd pneumonia-patient-processing-kjar
   $ mvn clean install
   `````

2. Start kafka (using community strimzi containers)
   `````
   $ podman-compose -f etc/docker-compose.yaml up -d
   `````


3. Build and Start app
   `````
   $ mvn clean package -DskipTests && \
         java -jar target/springboot-debezium-poc-0.0.1.jar
   `````

4. Optional:  Tear down pod:
   `````
   $ podman-compose -f etc/docker-compose.yaml down
   `````

## Test

1. Set environment variables to support testing:
   `````
   $ export KJAR_VERSION=1.0.3
   $ export KIE_SERVER_CONTAINER_NAME=fhir-bpm-service
   `````

2. Health Check Report
   `````
   $ curl -u "user:user" -H 'Accept:application/json' localhost:8080/rest/server/healthcheck?report=true
   `````

3. View swagger
   `````
   $ curl -v -u "user:user" localhost:8080/rest/swagger.json | jq .
   `````

4. Create a container in kie-server:
   `````
   $ sed "s/{KIE_SERVER_CONTAINER_NAME}/$KIE_SERVER_CONTAINER_NAME/g" etc/rhpam/kie_container.json \
     | sed "s/{KJAR_VERSION}/$KJAR_VERSION/g" \
     > /tmp/kie_container.json && \
     curl -u "user:user" -X PUT -H 'Content-type:application/json' localhost:8080/rest/server/containers/$KIE_SERVER_CONTAINER_NAME-$KJAR_VERSION -d '@/tmp/kie_container.json'
   `````

5. List containers
   `````
   $ curl -u "user:user" -X GET http://localhost:8080/rest/server/containers
   `````

6. Start a business process
   `````
   $ curl -X POST localhost:8080/fhir/processes/sendSampleCloudEvent/azra12350
   `````

7. List cases in JSON representation:
   `````
   $ curl -u "user:user" -X GET -H 'Accept:application/json' localhost:8080/rest/server/queries/cases/
   `````

8. List process definitions in JSON representation:
   `````
   $ curl -u "user:user" -X GET -H 'Accept:application/json' localhost:8080/rest/server/containers/$KIE_SERVER_CONTAINER_NAME-$KJAR_VERSION/processes/
   `````

Post Debezium configs:
    `````
    $ curl -X POST \
        -H "Accept:application/json" -H "Content-Type:application/json" \
        localhost:8083/connectors/ \
        -d "@etc/hapi-fhir/debezium-fhir-server-pgsql.json"
    `````

POST Observation to FHIR server
    `````
    $ curl -X POST \
       -H "Content-Type:application/fhir+json" \
       http://localhost:8080/fhir/Observation \
       -d "@src/test/resources/fhir/Observation1.json"
    `````

4. Build and Start app
   `````
   $ mvn clean package -DskipTests && \
         java -jar target/springboot-debezium-poc-0.0.1.jar
   `````

