package net.runelite.client.plugins._trackmyscape.ui;

import net.runelite.client.plugins._trackmyscape.SessionManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.RunnableExceptionLogger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.concurrent.ScheduledExecutorService;

class LoginForm extends JPanel {
    private final JLabel errorLabel;
    private final JTextField emailField;
    private final JTextField passwordField;
    private final JButton loginButton;

    LoginForm(final ScheduledExecutorService executor, final SessionManager sessionManager) {
        setLayout(new GridLayout(4, 1, 0, 7));

        errorLabel = addLabel("<html>Something went wrong, perhaps incorrect email/password combination.</html>");
        emailField = addComponent("Email", new JTextField());
        passwordField = addComponent("Password", new JPasswordField());
        loginButton = addButton("Login");

        loginButton.addActionListener(e -> {
            if (emailField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                return;
            }
            final String email = emailField.getText();
            final String password = passwordField.getText();
            errorLabel.setVisible(false);

            executor.execute(RunnableExceptionLogger.wrap(() -> sessionManager.login(email, password)));
        });
    }

    private JTextField addComponent(String label, final JTextField uiInput)
    {
        final JPanel container = new JPanel();
        container.setLayout(new BorderLayout());

        final JLabel uiLabel = new JLabel(label);

        uiLabel.setFont(FontManager.getRunescapeSmallFont());
        uiLabel.setBorder(new EmptyBorder(0, 0, 4, 0));
        uiLabel.setForeground(Color.WHITE);

        uiInput.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        uiInput.setBorder(new EmptyBorder(5, 7, 5, 7));

        container.add(uiLabel, BorderLayout.NORTH);
        container.add(uiInput, BorderLayout.CENTER);

        add(container);

        return uiInput;
    }

    private JButton addButton(String label) {
        JButton button = new JButton(label);

        add(button);

        return button;
    }

    private JLabel addLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.RED);
        label.setVisible(false);

        add(label);
        return label;
    }

    void fail() {
        errorLabel.setVisible(true);
    }

    void reset() {
        errorLabel.setVisible(false);
        emailField.setText("");
        passwordField.setText("");
    }
}
