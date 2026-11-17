package com.livestocktracker;

import javax.swing.*;

public class MainGUI extends JFrame {

    public MainGUI() {
        setTitle("Livestock Disease Tracker");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Record a Report", new ReportFormPanel());
        tabs.addTab("Manage Reports", placeholder("Reports table goes here"));
        tabs.addTab("Dashboard", placeholder("Charts and alerts go here"));
        tabs.addTab("Disease Guidance", placeholder("Disease guidance goes here"));

        add(tabs);
        setVisible(true);
        
    }

    private JPanel placeholder(String text) {
        JPanel panel = new JPanel();
        panel.add(new JLabel(text));
        return panel;
    }
}
