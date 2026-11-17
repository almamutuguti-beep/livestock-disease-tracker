package com.livestocktracker;

import db.DiseaseReportDAO;
import model.DiseaseReport;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ReportFormPanel extends JPanel {

    // TEMPORARY lists: replace with Person B's Validator constants once the group agrees on them.
    private static final String[] LIVESTOCK_TYPES = {"Cattle", "Goats", "Sheep", "Poultry", "Other"};
    private static final String[] STATUSES = {"suspected", "under review", "verified"};

    private final DiseaseReportDAO dao = new DiseaseReportDAO();

    private final JComboBox<ReferenceData.Item> diseaseBox = new JComboBox<>();
    private final JComboBox<ReferenceData.Item> regionBox = new JComboBox<>();
    private final JComboBox<String> typeBox = new JComboBox<>(LIVESTOCK_TYPES);
    private final JComboBox<String> statusBox = new JComboBox<>(STATUSES);
    private final JTextField dateField = new JTextField(LocalDate.now().toString(), 12);
    private final JTextField countField = new JTextField(12);
    private final JTextArea notesArea = new JTextArea(4, 25);

    private Runnable onSaved;

    public ReportFormPanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        List<ReferenceData.Item> diseases = ReferenceData.getDiseases();
        List<ReferenceData.Item> regions = ReferenceData.getRegions();
        for (ReferenceData.Item d : diseases) diseaseBox.addItem(d);
        for (ReferenceData.Item r : regions) regionBox.addItem(r);

        JLabel warning = new JLabel(" ");
        warning.setForeground(Color.RED);
        if (diseases.isEmpty() || regions.isEmpty()) {
            warning.setText("Could not load diseases/regions. Is MySQL running and is schema.sql loaded?");
        }
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        addRow(0, "", warning);
        addRow(1, "Disease:", diseaseBox);
        addRow(2, "Region:", regionBox);
        addRow(3, "Livestock type:", typeBox);
        addRow(4, "Report date (yyyy-MM-dd):", dateField);
        addRow(5, "Animals affected:", countField);
        addRow(6, "Status:", statusBox);
        addRow(7, "Notes (optional):", new JScrollPane(notesArea));

        JButton addButton = new JButton("Add Report");
        JButton clearButton = new JButton("Clear");
        addButton.addActionListener(e -> saveReport());
        clearButton.addActionListener(e -> clearForm());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(addButton);
        buttons.add(clearButton);
        addRow(8, "", buttons);

        // pushes everything to the top
        GridBagConstraints filler = new GridBagConstraints();
        filler.gridx = 0;
        filler.gridy = 9;
        filler.weighty = 1;
        add(Box.createVerticalGlue(), filler);
    }

    /** MainGUI can pass a refresh action here later, so the reports table updates after a save. */
    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    private void addRow(int row, String labelText, JComponent field) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;
        c.gridy = row;

        c.gridx = 0;
        add(new JLabel(labelText), c);

        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(field, c);
    }

    private void saveReport() {
        ReferenceData.Item disease = (ReferenceData.Item) diseaseBox.getSelectedItem();
        ReferenceData.Item region = (ReferenceData.Item) regionBox.getSelectedItem();
        if (disease == null || region == null) {
            warn("The disease and region lists are empty. Is MySQL running and is schema.sql loaded?");
            return;
        }

        String dateText = dateField.getText().trim();
        String countText = countField.getText().trim();

        if (dateText.isEmpty()) {
            warn("Please enter the report date.");
            dateField.requestFocus();
            return;
        }
        LocalDate date;
        try {
            date = LocalDate.parse(dateText);
        } catch (DateTimeParseException ex) {
            warn("The date must look like 2026-11-17.");
            dateField.requestFocus();
            return;
        }
        if (date.isAfter(LocalDate.now())) {
            warn("The report date cannot be in the future.");
            dateField.requestFocus();
            return;
        }

        if (countText.isEmpty()) {
            warn("Please enter the number of animals affected.");
            countField.requestFocus();
            return;
        }
        int count;
        try {
            count = Integer.parseInt(countText);
        } catch (NumberFormatException ex) {
            warn("Animals affected must be a whole number.");
            countField.requestFocus();
            return;
        }
        if (count <= 0) {
            warn("Animals affected must be greater than zero.");
            countField.requestFocus();
            return;
        }

        DiseaseReport report = new DiseaseReport(
                0,
                disease.getId(),
                region.getId(),
                (String) typeBox.getSelectedItem(),
                date,
                count,
                (String) statusBox.getSelectedItem());
        String notes = notesArea.getText().trim();
        if (!notes.isEmpty()) {
            report.setNotes(notes);
        }

        if (dao.addReport(report)) {
            JOptionPane.showMessageDialog(this, "Report saved.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            if (onSaved != null) onSaved.run();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Could not save the report. Check that MySQL is running.",
                    "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void warn(String message) {
        JOptionPane.showMessageDialog(this, message, "Check your input", JOptionPane.WARNING_MESSAGE);
    }

    private void clearForm() {
        if (diseaseBox.getItemCount() > 0) diseaseBox.setSelectedIndex(0);
        if (regionBox.getItemCount() > 0) regionBox.setSelectedIndex(0);
        typeBox.setSelectedIndex(0);
        statusBox.setSelectedIndex(0);
        dateField.setText(LocalDate.now().toString());
        countField.setText("");
        notesArea.setText("");
    }
}