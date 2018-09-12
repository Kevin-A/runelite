package net.runelite.client.plugins._trackmyscape.ui;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins._trackmyscape.SessionManager;
import net.runelite.client.plugins._trackmyscape.events.TMSLoginEvent;
import net.runelite.client.plugins._trackmyscape.events.TMSLoginFailedEvent;
import net.runelite.client.plugins._trackmyscape.events.TMSLogoutEvent;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@RequiredArgsConstructor
public class TrackMyScapePanel extends PluginPanel {

    private final EventBus eventBus;
    private final SessionManager sessionManager;
    private final ScheduledExecutorService executor;

    private LoginForm loginForm;
    private AccountPanel accountPanel;
    private GridBagConstraints c = new GridBagConstraints();

    public void init(SessionManager sessionManager, ScheduledExecutorService executor) {
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setLayout(new GridBagLayout());

        eventBus.register(this);

        initLayout(sessionManager, executor);
    }

    private void initLayout(SessionManager sessionManager, ScheduledExecutorService executor) {

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;

        setBackground(ColorScheme.DARK_GRAY_COLOR);

        final Header header = new Header();

        loginForm = new LoginForm(executor, sessionManager);
        loginForm.setBorder(new EmptyBorder(15, 0, 15, 0));

        accountPanel = new AccountPanel(sessionManager, executor);

        add(header, c);
        c.gridy++;

        add(loginForm, c);
        c.gridy++;

        add(accountPanel, c);
        c.gridy++;

        if (sessionManager.getCurrentSession() == null) {
            accountPanel.setVisible(false);
        } else {
            loginForm.setVisible(false);
        }
    }

    @Subscribe
    public void loggedIntoTMS(final TMSLoginEvent loggedInEvent) {
        log.debug("User logged into TMS, changing to account panel");
        loginForm.setVisible(false);
        accountPanel.setVisible(true);
        accountPanel.updateSession();
    }

    @Subscribe
    public void loginFailed(final TMSLoginFailedEvent tmsLoginFailedEvent) {
        log.debug("Failed to log in");
        loginForm.fail();
    }

    @Subscribe
    public void loggedOutOfTMS(final TMSLogoutEvent loggedOutEvent) {
        log.debug("User logged out of TMS, changing to login form");
        accountPanel.setVisible(false);
        loginForm.setVisible(true);
        loginForm.reset();
    }
}
