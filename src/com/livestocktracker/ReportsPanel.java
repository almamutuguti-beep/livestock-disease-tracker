package com.livestocktracker;

import db.DiseaseReportDAO;
import model.DiseaseReport;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportsPanel extends JPanel {

    private final DiseaseReportDAO dao = new DiseaseReportDAO();

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Disease", "Region", "Livestock", "Date", "Affected", "Status", "Notes"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);

    private final Map<Integer, String> diseaseNames = new HashMap<>();
    private final Map<Integer, String> regionNames = new HashMap<>();
    private List<DiseaseReport> reports = new ArrayList<>();

    public ReportsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        JButton updateButton = new JButton("Update Selected");
        JButton deleteButton = new JButton("Delete Selected");
        refreshButton.addActionListener(e -> refresh());
        updateButton.addActionListener(e -> editSelected());
        deleteButton.addActionListener(e -> deleteSelected());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(refreshButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        add(buttons, BorderLayout.SOUTH);

        refresh();
    }

    /** Reloads everything from MySQL and redraws the table. */
    public void refresh() {
        diseaseNames.clear();
        regionNames.clear();
        for (ReferenceData.Item d : ReferenceData.getDiseases()) diseaseNames.put(d.getId(), d.getName());
        for (ReferenceData.Item r : ReferenceData.getRegions()) regionNames.put(r.getId(), r.getName());

        reports = dao.getAllReports();
        model.setRowCount(0);
        for (DiseaseReport r : reports) {
            model.addRow(new Object[]{
                    r.getReportId(),
                    diseaseNames.getOrDefault(r.getDiseaseId(), "#" + r.getDiseaseId()),
                    regionNames.getOrDefault(r.getRegionId(), "#" + r.getRegionId()),
                    r.getLivestockType(),
                    r.getReportDate(),
                    r.getAffectedCount(),
                    r.getReportStatus(),
                    r.getNotes() == null ? "" : r.getNotes()
            });
        }
    }

    /** Finds the selected report by its database ID, not by row position. */
    private DiseaseReport getSelectedReport() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a report in the table first.",
                    "No report selected", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int id = (Integer) model.getValueAt(table.convertRowIndexToModel(row), 0);
        for (DiseaseReport r : reports) {
            if (r.getReportId() == id) return r;
        }
        return null;
    }

    private void editSelected() {
        DiseaseReport r = getSelectedReport();
        if (r == null) return;

        ReportFormPanel form = new ReportFormPanel();
        form.loadForEdit(r);

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Update Report",
                Dialog.ModalityType.APPLICATION_MODAL);
        form.setOnSaved(() -> {
            dialog.dispose();
            refresh();
        });
        dialog.add(form);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteSelected() {
        DiseaseReport r = getSelectedReport();
        if (r == null) return;

        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete report #" + r.getReportId() + "?",
                "Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;

        if (dao.deleteReport(r.getReportId())) {
            refresh();
        } else {
            JOptionPane.showMessageDialog(this, "Could not delete the report. Check that MySQL is running.",
                    "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }
}