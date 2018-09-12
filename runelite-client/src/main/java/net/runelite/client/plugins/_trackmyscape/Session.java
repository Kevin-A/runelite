package net.runelite.client.plugins._trackmyscape;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.UUID;

@Data
@EqualsAndHashCode(of = "uuid")
public class Session {
    private final UUID uuid;
    private final Instant created;
    private final String email;
}
