package io.streammachine.examples;

import io.streammachine.driver.client.StreamMachineClient;
import io.streammachine.driver.domain.Config;

public class ClientBuilder {
    public static StreamMachineClient createStreamMachineClient(String[] args) {
        if (args.length != 3) {
            System.out.println("Ensure that you've provided all required input arguments: [billingId, clientId, clientSecret]");
            System.exit(1);
        }

        var billingId = args[0];
        var clientId = args[1];
        var clientSecret = args[2];

        var config = Config.builder().build();

        return StreamMachineClient.builder()
                                  .billingId(billingId)
                                  .clientId(clientId)
                                  .clientSecret(clientSecret)
                                  .config(config)
                                  .build();
    }

}
