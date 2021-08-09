package io.streammachine.examples;

import io.streammachine.driver.client.StreamMachineClient;
import io.streammachine.driver.serializer.SerializationType;
import io.streammachine.schemas.demo.v1.DemoEvent;
import io.streammachine.schemas.demo.v1.StrmMeta;
import org.slf4j.Logger;

import java.util.Random;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.slf4j.LoggerFactory.getLogger;

public class Sender {

    private static final Logger LOG = getLogger(Sender.class);

    private static Random RANDOM = new Random();

    public static void main(String[] args) throws InterruptedException {
        new Sender().run(args);
    }

    /**
     * Generate a DemoEvent from a Java class that corresponds with a the streammachine/demo/1.0.0 schema.
     * These Java classes are generated and provided by Stream Machine, based on the
     * serialization schema.
     * <p>
     *
     * @return a {@link io.streammachine.schemas.StreamMachineEvent}
     */
    private static DemoEvent createAvroEvent() {
        int consentLevel = RANDOM.nextInt(4);

        return DemoEvent.newBuilder()
                .setStrmMeta(StrmMeta.newBuilder()
                        .setEventContractRef("streammachine/example/1.2.3")
                        .setConsentLevels(singletonList(consentLevel))
                        .build())
                .setUniqueIdentifier(UUID.randomUUID().toString())
                .setSomeSensitiveValue("A value that should be encrypted")
                .setConsistentValue("a-user-session")
                .setNotSensitiveValue("Hello from Java")
                .build();
    }

    /**
     * start sending hardcoded avro events.
     *
     * @param args 3 parameters: [billingId, clientId, clientSecret]
     * @throws InterruptedException
     */
    private void run(String[] args) throws InterruptedException {
        StreamMachineClient client = ClientBuilder.createStreamMachineClient(args);

        while (true) {
            var event = createAvroEvent();

            client.send(event, SerializationType.AVRO_BINARY)
                  .whenComplete((response, exception) -> {
                      if (exception != null) {
                          LOG.error("An exception occurred while trying to send an event to Stream Machine", exception);
                      }

                      if (response.getStatus() == 204) {
                          LOG.debug("{}", response.getStatus());
                      } else if (response.getStatus() == 400) {
                          // Try to change the value for the url field in the createAvroEvent method below to something that is not a url
                          // You can see that the Stream Machine gateway rejects the
                          // message, stating that the field does not match the regex
                          // provided in resources/schema/avro/strm.json
                          LOG.debug("Bad request: {}", response.getContentAsString());
                      }
                  });

            Thread.sleep(500);
        }
    }
}
