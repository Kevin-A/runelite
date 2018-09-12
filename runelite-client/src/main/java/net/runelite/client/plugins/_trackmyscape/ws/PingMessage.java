package net.runelite.client.plugins._trackmyscape.ws;

import lombok.Value;

import java.time.Instant;

@Value
public class PingMessage {
    private final String type = "PING";
    private Instant time;
}
