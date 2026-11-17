package com.livestocktracker;

import db.DiseaseReportDAO;
import model.DiseaseReport;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DashboardPanel extends JPanel {

    private final DiseaseReportDAO dao = new DiseaseReportDAO();

    private final JLabel totalReportsLabel = new JLabel("0", SwingConstants.CENTER);
    private final JLabel totalAffectedLabel = new JLabel("0", SwingConstants.CENTER);
    private final JLabel regionsLabel = new JLabel("0", SwingConstants.CENTER);
    private final JComboBox<String> viewBox = new JComboBox<>(new String[]{
            "Reports by region", "Reports by month", "Reports by livestock type"});
    private final JPanel chartHolder = new JPanel(new BorderLayout());

    public DashboardPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel cards = new JPanel(new GridLayout(1, 3, 10, 10));
        cards.add(card("Total reports", totalReportsLabel));
        cards.add(card("Total animals affected", totalAffectedLabel));
        cards.add(card("Regions with reports", regionsLabel));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refresh());
        viewBox.addActionListener(e -> showChart());

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(new JLabel("Chart:"));
        controls.add(viewBox);
        controls.add(refreshButton);

        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.add(cards, BorderLayout.NORTH);
        top.add(controls, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(chartHolder, BorderLayout.CENTER);

        refresh();
    }

    private JPanel card(String title, JLabel valueLabel) {
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 28f));
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        p.add(titleLabel, BorderLayout.NORTH);
        p.add(valueLabel, BorderLayout.CENTER);
        return p;
    }

    /** Reloads the summary numbers and the chart from MySQL. */
    public void refresh() {
        List<DiseaseReport> reports = dao.getAllReports();
        int totalAffected = 0;
        Set<Integer> regionIds = new HashSet<>();
        for (DiseaseReport r : reports) {
            totalAffected += r.getAffectedCount();
            regionIds.add(r.getRegionId());
        }
        totalReportsLabel.setText(String.valueOf(reports.size()));
        totalAffectedLabel.setText(String.valueOf(totalAffected));
        regionsLabel.setText(String.valueOf(regionIds.size()));
        showChart();
    }

    private void showChart() {
        List<Object[]> rows;
        String title;
        String xLabel;
        switch (viewBox.getSelectedIndex()) {
            case 1:
                rows = dao.getReportCountsByMonth();
                title = "Disease Reports by Month";
                xLabel = "Month";
                break;
            case 2:
                rows = dao.getCountsByLivestockType();
                title = "Disease Reports by Livestock Type";
                xLabel = "Livestock type";
                break;
            default:
                rows = dao.getReportCountsByRegion();
                title = "Disease Reports by Region";
                xLabel = "Region";
                break;
        }

        chartHolder.removeAll();
        if (rows.isEmpty()) {
            chartHolder.add(new JLabel("No reports yet. Add some on the Record a Report tab.",
                    SwingConstants.CENTER), BorderLayout.CENTER);
        } else {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (Object[] row : rows) {
                dataset.addValue(((Number) row[1]).intValue(), "Reports", (String) row[0]);
            }
            JFreeChart chart = ChartFactory.createBarChart(
                    title, xLabel, "Number of reports",
                    dataset, PlotOrientation.VERTICAL, false, true, false);
            ((NumberAxis) chart.getCategoryPlot().getRangeAxis())
                    .setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            chartHolder.add(new ChartPanel(chart), BorderLayout.CENTER);
        }
        chartHolder.revalidate();
        chartHolder.repaint();
    }
}