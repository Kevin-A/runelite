package net.runelite.client.plugins._trackmyscape.ui;

import net.runelite.client.plugins._trackmyscape.Session;
import net.runelite.client.plugins._trackmyscape.SessionManager;
import net.runelite.client.util.RunnableExceptionLogger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.concurrent.ScheduledExecutorService;

class AccountPanel extends JPanel {

    final SessionManager sessionManager;
    final JLabel emailLabel;

    AccountPanel(SessionManager sessionManager, ScheduledExecutorService executor) {
        setLayout(new BorderLayout());

        this.sessionManager = sessionManager;

        final Session session = sessionManager.getCurrentSession();

        if (session == null) {
            emailLabel = new JLabel("");
        } else {
            emailLabel = new JLabel("Logged in as " + session.getEmail());

        }
        emailLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        emailLabel.setForeground(Color.WHITE);

        final JButton logoutButton = new JButton("Log out");
        logoutButton.addActionListener(e -> executor.execute(RunnableExceptionLogger.wrap(sessionManager::logout)));

        add(emailLabel, BorderLayout.NORTH);
        add(logoutButton, BorderLayout.CENTER);
    }

    void updateSession() {
        final Session session = sessionManager.getCurrentSession();

        emailLabel.setText("Logged in as " + session.getEmail());
        emailLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        emailLabel.setForeground(Color.WHITE);
    }
}
