package io.streammachine.examples;

import io.streammachine.driver.client.StreamMachineClient;
import io.streammachine.driver.domain.Config;
import io.streammachine.driver.serializer.SerializationType;
import io.streammachine.schemas.strmcatalog.clickstream.ClickstreamEvent;
import io.streammachine.schemas.strmcatalog.clickstream.Customer;
import io.streammachine.schemas.strmcatalog.clickstream.StrmMeta;
import lombok.extern.slf4j.Slf4j;

import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@Slf4j
public class Sender {
    public static void main(String[] args) throws InterruptedException, URISyntaxException {
        new Sender().run(args);
    }

    /**
     * start sending hardcoded avro events.
     *
     * @param args 3 parameters: [billingId, clientId, clientSecret]
     * @throws InterruptedException
     */
    private void run(String[] args) throws InterruptedException, URISyntaxException {
        if (args.length != 3) {
            System.out.println("Ensure that you've provided all required input arguments: [billingId, clientId, clientSecret]");
            System.exit(1);
        }

        var billingId = args[0];
        var clientId = args[1];
        var clientSecret = args[2];

        var config = Config.builder().build();
        StreamMachineClient client = StreamMachineClient.builder()
                .billingId(billingId)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .config(config)
                .build();

        while (true) {
            var event = createAvroEvent();
            final CompletableFuture<HttpResponse<String>> completableFuture = client.send(event, SerializationType.AVRO_BINARY);
            HttpResponse<String> response = completableFuture.join();
            log.debug("{}", response);
            Thread.sleep(200);
        }
    }

    /**
     * Generate a ClickstreamEvent from a Java class that corresponds with a the clickstream schema.
     * These Java classes are generated and provided by Streammachine, based on the
     * schema definition and the serialization schema.
     * <p>
     * This particular event corresponds to an Avro schema that you can see in resources/schema/avro/schema.avsc and resources/schema/avro/strm.json
     *
     * @return a {@link io.streammachine.driver.domain.StreamMachineEvent}
     */
    private static ClickstreamEvent createAvroEvent() {
        return ClickstreamEvent.newBuilder()
                .setAbTests(singletonList("abc"))
                .setEventType("button x clicked")
                .setCustomer(Customer.newBuilder()
                        .setId("some-identifier")
                        .build())
                .setReferrer("https://www.streammachine.io")
                .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.83 Safari/537.36")
                .setProducerSessionId("session-01")
                .setConversion(1)
                .setStrmMeta(StrmMeta.newBuilder()
                        .setTimestamp(System.currentTimeMillis())
                        .setSchemaId("clickstream")
                        .setNonce(0)
                        .setTimestamp(System.currentTimeMillis())
                        .setConsentLevels(asList(0, 1, 2))
                        .build())
                .setUrl("https://portal.streammachine.io")
                .build();
    }
}
