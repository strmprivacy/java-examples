package io.streammachine.examples;

import io.streammachine.driver.client.StreamMachineClient;
import io.streammachine.driver.common.WebSocketConsumer;
import io.streammachine.driver.domain.Config;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.websocket.api.Session;

@Slf4j
public class Receiver {
    public static void main(String[] args) {
        new Receiver().run(args);
    }

    private void run(String[] args) {
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

        ContentResponse isAlive = client.egressIsAlive();

        log.debug("{}: {}", isAlive.getStatus(), isAlive.getReason());

        client.startReceivingWs(true, new StreamMachineEventWsListener());
    }

    private static class StreamMachineEventWsListener extends WebSocketConsumer {
        @Override
        public void onWebSocketClose(int statusCode, String reason) {
            log.info("Closed websocked connection...");
            log.info("Code: {}", statusCode);
            log.info("Reason: {}", reason);
        }

        @Override
        public void onWebSocketConnect(Session sess) {
            log.info("Opened websocket connection...");
        }

        @Override
        public void onWebSocketError(Throwable cause) {
            log.error("An error occurred", cause);
        }

        @Override
        public void onWebSocketText(String message) {
            System.out.println(message);
        }
    }
}


