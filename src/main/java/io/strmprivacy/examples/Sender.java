package io.strmprivacy.examples;

import io.strmprivacy.driver.client.StrmPrivacyClient;
import io.strmprivacy.driver.domain.Config;
import io.strmprivacy.schemas.demo.v1.DemoEvent;
import io.strmprivacy.schemas.demo.v1.StrmMeta;
import org.slf4j.Logger;

import java.util.Random;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.slf4j.LoggerFactory.getLogger;

public class Sender {

    private static final Logger LOG = getLogger(Sender.class);

    private static final Random RANDOM = new Random();

    public static void main(String[] args) throws InterruptedException {
        new Sender().run(args);
    }

    /**
     * start sending hardcoded avro events.
     *
     * @param args 3 parameters: [clientId, clientSecret]
     */
    private void run(String[] args) throws InterruptedException {
        if (args.length != 2) {
            System.out.println("Ensure that you've provided all required input arguments: [clientId, clientSecret]");
            System.exit(1);
        }

        var clientId = args[0];
        var clientSecret = args[1];

        var config = Config.builder().build();

        StrmPrivacyClient client = StrmPrivacyClient.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .config(config)
                .build();

        while (true) {
            var event = createAvroEvent();

            client.send(event)
                    .whenComplete((response, exception) -> {
                        if (exception != null) {
                            LOG.error("An exception occurred while trying to send an event to STRM Privacy", exception);
                        }

                        if (response.getStatus() == 204) {
                            LOG.debug("Successfully sent event");
                        } else if (response.getStatus() == 400) {
                            LOG.debug("Bad request: {}", response.getContentAsString());
                        }
                    });

            Thread.sleep(500);
        }
    }

    /**
     * Generate a DemoEvent from a Java class that corresponds with a the strmprivacy/demo/1.0.2 schema.
     * These Java classes are generated and provided by STRM Privacy, based on the
     * serialization schema.
     * <p>
     *
     * @return a {@link io.strmprivacy.schemas.StrmPrivacyEvent}
     */
    private static DemoEvent createAvroEvent() {
        int consentLevel = RANDOM.nextInt(4);

        return DemoEvent.newBuilder()
                .setStrmMeta(StrmMeta.newBuilder()
                        .setEventContractRef("strmprivacy/example/1.3.0")
                        .setConsentLevels(singletonList(consentLevel))
                        .build())
                .setUniqueIdentifier(UUID.randomUUID().toString())
                .setSomeSensitiveValue("A value that should be encrypted")
                .setConsistentValue("a-user-session")
                .setNotSensitiveValue("Hello from Java")
                .build();
    }
}
