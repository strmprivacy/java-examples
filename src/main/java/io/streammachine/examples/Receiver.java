package io.streammachine.examples;

import io.streammachine.driver.client.StreamMachineClient;
import io.streammachine.driver.domain.Config;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

@Slf4j
public class Receiver {
    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        new Receiver().run(args);
    }

    private void run(String[] args) throws IOException, InterruptedException, URISyntaxException {

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

        HttpResponse<String> isAlive = client.egressIsAlive();
        log.debug("{}", isAlive);
        client.startReceivingSse(true, event -> System.out.println(event.readData()));
    }
}
