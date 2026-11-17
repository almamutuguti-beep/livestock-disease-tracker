package service;

import db.DiseaseReportDAO;
import model.DiseaseReport;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Preliminary threshold check. It counts REPORT RECORDS (not affected animals)
 * for the same disease and region inside the time window.
 * A flag is NOT a confirmed outbreak; it only means the stored reports met the rule.
 */
public class OutbreakChecker {

    /** Flag when a disease+region has at least this many reports in the window. */
    public static final int THRESHOLD = 5;

    /** Window length in calendar days, counting today (today and the 6 days before it). */
    public static final int WINDOW_DAYS = 7;

    private final DiseaseReportDAO dao;

    public OutbreakChecker() {
        this(new DiseaseReportDAO());
    }

    public OutbreakChecker(DiseaseReportDAO dao) {
        this.dao = dao;
    }

    /** One message per flagged disease+region, using today's date. */
    public List<String> checkAll() {
        return checkReports(dao.getAllReports(), LocalDate.now());
    }

    /**
     * The same check on a given list and date, with no database involved.
     * Useful for testing and for demonstrating with a chosen date.
     */
    public List<String> checkReports(List<DiseaseReport> reports, LocalDate today) {
        LocalDate windowStart = today.minusDays(WINDOW_DAYS - 1);

        // Group by disease id + region id, remembering one report for the display names
        Map<String, Integer> counts = new LinkedHashMap<>();
        Map<String, DiseaseReport> sample = new LinkedHashMap<>();
        for (DiseaseReport r : reports) {
            LocalDate d = r.getReportDate();
            if (d == null || d.isBefore(windowStart) || d.isAfter(today)) continue;
            String key = r.getDiseaseId() + "|" + r.getRegionId();
            counts.merge(key, 1, Integer::sum);
            sample.putIfAbsent(key, r);
        }

        // Highest counts first
        List<Map.Entry<String, Integer>> flagged = new ArrayList<>();
        for (Map.Entry<String, Integer> e : counts.entrySet()) {
            if (e.getValue() >= THRESHOLD) flagged.add(e);
        }
        flagged.sort((a, b) -> b.getValue() - a.getValue());

        List<String> messages = new ArrayList<>();
        for (Map.Entry<String, Integer> e : flagged) {
            DiseaseReport r = sample.get(e.getKey());
            String disease = r.getDiseaseName() != null ? r.getDiseaseName()
                                                         : "disease #" + r.getDiseaseId();
            String region = r.getRegionName() != null ? r.getRegionName()
                                                       : "region #" + r.getRegionId();
            messages.add("Alert threshold reached, seek verification: "
                    + e.getValue() + " reports of " + disease + " in " + region
                    + " in the last " + WINDOW_DAYS + " days.");
        }
        return messages;
    }
}
