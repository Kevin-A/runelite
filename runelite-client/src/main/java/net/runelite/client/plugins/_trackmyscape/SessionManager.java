package net.runelite.client.plugins._trackmyscape;

import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLite;
import net.runelite.client.plugins._trackmyscape.events.TMSLoginEvent;
import net.runelite.client.plugins._trackmyscape.events.TMSLoginFailedEvent;
import net.runelite.client.plugins._trackmyscape.events.TMSLogoutEvent;
import net.runelite.client.plugins.loottracker.LootReceived;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@RequiredArgsConstructor
public class SessionManager {
    private static final File SESSION_FILE = new File(RuneLite.RUNELITE_DIR, "tms_session");

    private WSClient wsClient;

    @Getter
    private Session currentSession;

    private final TMSClient tmsClient = new TMSClient();
    private final ScheduledExecutorService executor;
    private final EventBus eventBus;

    void loadSession() {
        if (!SESSION_FILE.exists())
        {
            log.info("No session for TMS file exists");
            return;
        }

        Session session;

        try (FileInputStream in = new FileInputStream(SESSION_FILE))
        {
            session = new Gson().fromJson(new InputStreamReader(in), Session.class);

            log.debug("Loaded session for {}", session.getEmail());
        }
        catch (Exception ex)
        {
            log.warn("Unable to load session file", ex);
            // TODO keep trying?
            return;
        }

        TMSClient tmsClient = new TMSClient(session.getUuid());
        if (!tmsClient.isSessionActive()) {
            log.debug("Loaded session {} is invalid, not opening session", session.getUuid());
            eventBus.post(new TMSLogoutEvent());
            return;
        }

        eventBus.post(new TMSLoginEvent());
        openSession(session);
    }

    private void saveSession() {
        if (currentSession == null) {
            return;
        }

        try(FileWriter fw = new FileWriter(SESSION_FILE)) {
            log.debug(new Gson().toJson(currentSession));
            new Gson().toJson(currentSession, fw);
            log.debug("Saved TMS session to {}", SESSION_FILE);
        } catch (IOException e) {
            log.warn("Could not store TMS session to file", e);
        }
    }

    private void deleteSession() { SESSION_FILE.delete(); }

    private void openSession(Session session) {
        // If the ws session already exists, don't need to do anything
        if (wsClient == null || !wsClient.checkSession(session))
        {
            if (wsClient != null)
            {
                wsClient.close();
            }

            wsClient = new WSClient(eventBus, executor, session);
            wsClient.connect();
        }

        currentSession = session;
        log.info("Opened session with TMS");
    }

    private void closeSession() {
        if (wsClient != null) {
            wsClient.close();
            wsClient = null;
        }

        if (currentSession == null) {
            return;
        }

        log.debug("Logging out of account {}", currentSession.getEmail());
        TMSClient tmsClient = new TMSClient(currentSession.getUuid());
        try {
            tmsClient.logout();
            eventBus.post(new TMSLogoutEvent());
        } catch (IOException e) {
            log.warn("Unable to log out of session", e);
        }

        currentSession = null;
    }

    public void login(String email, String password) {
        final LoginResponse response;
        try {
            response = tmsClient.login(email, password);
            log.debug("Logged in with {} and received session uuid: {}", email, response.getUuid());
        } catch (Exception e) {
            eventBus.post(new TMSLoginFailedEvent());
            return;
        }

        openSession(new Session(response.getUuid(), Instant.now(), response.getEmail()));
        saveSession();
        eventBus.post(new TMSLoginEvent());
    }

    public void logout() {
        closeSession();
        deleteSession();
    }

    void send(LootReceived lootReceived) {
        wsClient.send(lootReceived);
    }
}