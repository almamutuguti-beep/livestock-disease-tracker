package com.livestocktracker;

import javax.swing.*;

public class MainGUI extends JFrame {

    public MainGUI() {
        setTitle("Livestock Disease Tracker");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ReportFormPanel formPanel = new ReportFormPanel();
        ReportsPanel reportsPanel = new ReportsPanel();
        DashboardPanel dashboardPanel = new DashboardPanel();
        formPanel.setOnSaved(() -> {
            reportsPanel.refresh();
            dashboardPanel.refresh();
        });

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Record a Report", formPanel);
        tabs.addTab("Manage Reports", reportsPanel);
        tabs.addTab("Dashboard", dashboardPanel);
        tabs.addTab("Disease Guidance", placeholder("Disease guidance goes here"));
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedComponent() == reportsPanel) reportsPanel.refresh();
            if (tabs.getSelectedComponent() == dashboardPanel) dashboardPanel.refresh();
        });

        add(tabs);
        setVisible(true);
    }

    private JPanel placeholder(String text) {
        JPanel panel = new JPanel();
        panel.add(new JLabel(text));
        return panel;
    }
}