package io.strmprivacy.examples;

import io.strmprivacy.driver.client.StrmPrivacyClient;
import io.strmprivacy.driver.domain.Config;

public class ClientBuilder {
    public static StrmPrivacyClient createStrmPrivacyClient(String[] args) {
        if (args.length != 3) {
            System.out.println("Ensure that you've provided all required input arguments: [billingId, clientId, clientSecret]");
            System.exit(1);
        }

        var billingId = args[0];
        var clientId = args[1];
        var clientSecret = args[2];

        var config = Config.builder().build();

        return StrmPrivacyClient.builder()
                .billingId(billingId)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .config(config)
                .build();
    }

}
