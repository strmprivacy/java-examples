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
     * @param args 3 parameters: [billingId, clientId, clientSecret]
     */
    private void run(String[] args) throws InterruptedException {
        if (args.length != 3) {
            System.out.println("Ensure that you've provided all required input arguments: [billingId, clientId, clientSecret]");
            System.exit(1);
        }

        var billingId = args[0];
        var clientId = args[1];
        var clientSecret = args[2];

        var config = Config.builder().build();

        StrmPrivacyClient client = StrmPrivacyClient.builder()
                .billingId(billingId)
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
                            LOG.debug("{}", response.getStatus());
                        } else if (response.getStatus() == 400) {
                            // Try to change the value for the url field in the createAvroEvent method below to something that is not a url
                            // You can see that the STRM Privacy gateway rejects the
                            // message, stating that the field does not match the regex
                            // provided in resources/schema/avro/strm.json
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
                        .setEventContractRef("strmprivacy/example/1.2.3")
                        .setConsentLevels(singletonList(consentLevel))
                        .build())
                .setUniqueIdentifier(UUID.randomUUID().toString())
                .setSomeSensitiveValue("A value that should be encrypted")
                .setConsistentValue("a-user-session")
                .setNotSensitiveValue("Hello from Java")
                .build();
    }
}
