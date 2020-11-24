package io.streammachine.examples;

import io.streammachine.driver.client.StreamMachineClient;
import io.streammachine.driver.domain.Config;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.Response;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketListener;

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

        Response isAlive = client.egressIsAlive();
        log.debug("{}: {}", isAlive.getStatusCode(), isAlive.getStatusText());

        client.startReceivingWs(true, new StreamMachineEventWsListener());
    }

    private static class StreamMachineEventWsListener implements WebSocketListener {

        @Override
        public void onOpen(WebSocket websocket) {
            log.info("Opened websocket connection...");
        }

        @Override
        public void onClose(WebSocket websocket, int code, String reason) {
            log.info("Closed websocked connection...");
        }

        @Override
        public void onTextFrame(String payload, boolean finalFragment, int rsv) {
            System.out.println(payload);
        }

        @Override
        public void onError(Throwable t) {
            log.error("An error occurred", t);
        }
    }

}


