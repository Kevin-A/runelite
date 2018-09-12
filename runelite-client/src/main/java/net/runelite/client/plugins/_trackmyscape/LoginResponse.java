package net.runelite.client.plugins._trackmyscape;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
class LoginResponse {
    private UUID uuid;
    private String email;
}
