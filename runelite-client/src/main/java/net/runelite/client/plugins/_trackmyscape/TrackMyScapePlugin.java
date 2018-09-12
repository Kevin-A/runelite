package net.runelite.client.plugins._trackmyscape;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins._trackmyscape.ui.TrackMyScapePanel;
import net.runelite.client.plugins.loottracker.LootReceived;
import net.runelite.client.plugins.loottracker.LootTrackerPlugin;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import okhttp3.*;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@PluginDescriptor(
        name = "Track My Scape",
        description = "Integrate with TMS to track your drops and experience",
        tags = {"tms", "integration", "external"}
//        enabledByDefault = false
)
@PluginDependency(LootTrackerPlugin.class)
public class TrackMyScapePlugin extends Plugin {

    @Getter(AccessLevel.PACKAGE)
    private NavigationButton button;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private Client client;

    @Inject
    private ScheduledExecutorService executor;

    @Inject
    private EventBus eventBus;

    private SessionManager sessionManager;
    private TrackMyScapePanel panel;

    @Override
    protected void startUp() throws Exception {
        sessionManager = new SessionManager(executor, eventBus);
        sessionManager.loadSession();

        panel = new TrackMyScapePanel(eventBus, sessionManager, executor);
        panel.init(sessionManager, executor);

        BufferedImage icon =  ImageUtil.getResourceStreamFromClass(getClass(), "ge_icon.png");

        button = NavigationButton.builder()
                .tooltip("Track My Scape")
                .priority(10)
                .icon(icon)
                .panel(panel)
                .build();

        clientToolbar.addNavigation(button);
    }

    @Override
    protected void shutDown()
    {
        clientToolbar.removeNavigation(button);
    }

    @Subscribe
    public void onLootReceived(final LootReceived loot) {
        log.info("{} {} {}", loot.getName(), loot.getCombat(), loot.getItems());
        loot.setPlayerUsername(client.getUsername());
        sessionManager.send(loot);
    }
}
