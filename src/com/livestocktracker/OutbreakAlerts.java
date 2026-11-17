package com.livestocktracker;

import db.DiseaseReportDAO;
import model.DiseaseReport;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OutbreakAlerts {

    public static final int THRESHOLD = 5;
    public static final int WINDOW_DAYS = 7;

    /** One message per disease+region pair with THRESHOLD or more reports in the last WINDOW_DAYS days. */
    public static List<String> check() {
        Map<Integer, String> diseaseNames = new HashMap<>();
        Map<Integer, String> regionNames = new HashMap<>();
        for (ReferenceData.Item d : ReferenceData.getDiseases()) diseaseNames.put(d.getId(), d.getName());
        for (ReferenceData.Item r : ReferenceData.getRegions()) regionNames.put(r.getId(), r.getName());

        LocalDate since = LocalDate.now().minusDays(WINDOW_DAYS);
        Map<List<Integer>, Integer> counts = new LinkedHashMap<>();
        for (DiseaseReport r : new DiseaseReportDAO().getAllReports()) {
            if (r.getReportDate().isBefore(since)) continue;
            counts.merge(List.of(r.getDiseaseId(), r.getRegionId()), 1, Integer::sum);
        }

        List<String> alerts = new ArrayList<>();
        for (Map.Entry<List<Integer>, Integer> e : counts.entrySet()) {
            if (e.getValue() >= THRESHOLD) {
                String disease = diseaseNames.getOrDefault(e.getKey().get(0), "Disease #" + e.getKey().get(0));
                String region = regionNames.getOrDefault(e.getKey().get(1), "Region #" + e.getKey().get(1));
                alerts.add("ALERT threshold reached: " + disease + " in " + region + " - "
                        + e.getValue() + " reports in the last " + WINDOW_DAYS
                        + " days. Please seek veterinary verification.");
            }
        }
        return alerts;
    }
}