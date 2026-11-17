package com.livestocktracker;

import javax.swing.*;
import java.awt.*;

public class GuidancePanel extends JPanel {

    private final DefaultListModel<ReferenceData.Guidance> listModel = new DefaultListModel<>();
    private final JList<ReferenceData.Guidance> list = new JList<>(listModel);
    private final JTextArea details = new JTextArea();

    public GuidancePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel note = new JLabel("<html>General awareness only. This application does not diagnose animals "
                + "or confirm outbreaks. Always contact a licensed veterinarian or your local "
                + "animal-health officer.</html>");
        note.setForeground(new Color(150, 60, 0));
        add(note, BorderLayout.NORTH);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showSelected();
        });

        details.setEditable(false);
        details.setLineWrap(true);
        details.setWrapStyleWord(true);
        details.setFont(details.getFont().deriveFont(14f));
        details.setMargin(new Insets(10, 10, 10, 10));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(list), new JScrollPane(details));
        split.setDividerLocation(260);
        add(split, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refresh());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(refreshButton);
        add(bottom, BorderLayout.SOUTH);

        refresh();
    }

    public void refresh() {
        listModel.clear();
        for (ReferenceData.Guidance g : ReferenceData.getGuidance()) {
            listModel.addElement(g);
        }
        if (listModel.isEmpty()) {
            details.setText("No diseases found. Is MySQL running and is schema.sql loaded?");
        } else {
            list.setSelectedIndex(0);
        }
    }

    private void showSelected() {
        ReferenceData.Guidance g = list.getSelectedValue();
        if (g == null) return;
        details.setText(g.getName()
                + "\n\nAbout\n" + nz(g.getDescription())
                + "\n\nRecommended action\n" + nz(g.getAction()));
        details.setCaretPosition(0);
    }

    private String nz(String s) {
        return s == null ? "No information recorded." : s;
    }
}