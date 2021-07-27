package io.streammachine.examples;

import io.streammachine.driver.client.StreamMachineClient;
import io.streammachine.driver.serializer.SerializationType;
import io.streammachine.schemas.strmcatalog.clickstream.ClickstreamEvent;
import io.streammachine.schemas.strmcatalog.clickstream.Customer;
import io.streammachine.schemas.strmcatalog.clickstream.StrmMeta;
import org.slf4j.Logger;

import java.util.Random;

import static java.util.Collections.singletonList;
import static org.slf4j.LoggerFactory.getLogger;

public class Sender {

    private static final Logger LOG = getLogger(Sender.class);

    private static Random RANDOM = new Random();

    public static void main(String[] args) throws InterruptedException {
        new Sender().run(args);
    }

    /**
     * Generate a ClickstreamEvent from a Java class that corresponds with a the clickstream schema.
     * These Java classes are generated and provided by Streammachine, based on the
     * schema definition and the serialization schema.
     * <p>
     * This particular event corresponds to an Avro schema that you can see in resources/schema/avro/schema.avsc and
     * resources/schema/avro/strm.json
     *
     * @return a {@link io.streammachine.schemas.StreamMachineEvent}
     */
    private static ClickstreamEvent createAvroEvent() {
        int consentLevel = RANDOM.nextBoolean() ? 1 : 0;

        return ClickstreamEvent.newBuilder()
                               .setAbTests(singletonList("abc"))
                               .setEventType("button x clicked")
                               .setCustomer(Customer.newBuilder()
                                                    .setId("some-identifier")
                                                    .build())
                               .setReferrer("https://www.streammachine.io")
                               .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_6) AppleWebKit/537.36 (KHTML, like Gecko) " +
                                       "Chrome/85.0.4183.83 Safari/537.36")
                               .setProducerSessionId("session-01")
                               .setConversion(1)
                               .setStrmMeta(StrmMeta.newBuilder()
                                                    .setEventContractRef("streammachine/clickstream/0.3.0")
                                                    .setConsentLevels(singletonList(consentLevel))
                                                    .build())
                               .setUrl("https://portal.streammachine.io")
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
