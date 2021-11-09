package io.strmprivacy.examples;

import io.strmprivacy.driver.client.StrmPrivacyClient;
import io.strmprivacy.driver.common.WebSocketConsumer;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class Receiver {

    private static final Logger LOG = getLogger(Receiver.class);

    public static void main(String[] args) {
        new Receiver().run(args);
    }

    private void run(String[] args) {
        StrmPrivacyClient client = ClientBuilder.createStrmPrivacyClient(args);

        try {
            ContentResponse isAlive = client.egressIsAlive();

            LOG.debug("{}: {}", isAlive.getStatus(), isAlive.getReason());

            client.startReceivingWs(true, new StrmPrivacyEventWsListener());
        } catch (Exception e) {
            LOG.error("Exception checking isAlive: " + e.getMessage(), e);
            client.stop();
        }
    }

    private static class StrmPrivacyEventWsListener extends WebSocketConsumer {
        @Override
        public void onWebSocketClose(int statusCode, String reason) {
            LOG.info("Closed websocked connection...");
            LOG.info("Code: {}", statusCode);
            LOG.info("Reason: {}", reason);
        }

        @Override
        public void onWebSocketConnect(Session sess) {
            LOG.info("Opened websocket connection...");
        }

        @Override
        public void onWebSocketError(Throwable cause) {
            LOG.error("An error occurred", cause);
        }

        @Override
        public void onWebSocketText(String message) {
            System.out.println(message);
        }
    }
}


