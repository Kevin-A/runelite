package net.runelite.client.plugins._trackmyscape.ui;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Header extends JPanel {

    public Header() {
        setLayout(new BorderLayout());

        final JLabel uiLabel = new JLabel("Track My Scape");

        uiLabel.setFont(new Font("title", Font.BOLD, 20));
        uiLabel.setBorder(new EmptyBorder(0, 0, 4, 0));
        uiLabel.setForeground(Color.WHITE);

        final JLabel description = new JLabel(
                "<html>" +
                        "<p>" +
                            "Track My Scape is a website where you can keep track of your drops, using RuneLite's loot " +
                            "tracker plugin. Upon receiving loot, from NPC, barrows, clue, raids, anything that RuneLite is " +
                            "currently able to track, the loot will be sent to Track My Scape." +
                        "</p>" +
                        "<p>" +
                            "If you would like to keep track of your loot you will need to create an account on TrackMyScape.com" +
                        "</p>" +
                      "</html>");
        description.setBorder(new EmptyBorder(0, 0, 4, 0));
        description.setForeground(Color.WHITE);

        add(uiLabel, BorderLayout.NORTH);
        add(description, BorderLayout.CENTER);
    }
}
