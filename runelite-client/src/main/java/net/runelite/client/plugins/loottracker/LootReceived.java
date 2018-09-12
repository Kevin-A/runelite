package net.runelite.client.plugins.loottracker;

import lombok.Data;

@Data
public class LootReceived {
    private final String type = "LOOT";
    private final String name;
    private final int combat;
    private final LootTrackerItem[] items;
    private final EntityType entity;
    private String playerUsername;
}
