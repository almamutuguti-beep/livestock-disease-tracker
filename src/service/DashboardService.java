package service;

import db.DiseaseReportDAO;
import model.DiseaseReport;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The only class the dashboard panel should call. It keeps the calculations out of Swing.
 * Maps are LinkedHashMaps so chart order stays stable.
 */
public class DashboardService {

    private final DiseaseReportDAO dao;

    public DashboardService() {
        this(new DiseaseReportDAO());
    }

    public DashboardService(DiseaseReportDAO dao) {
        this.dao = dao;
    }

    public int getTotalReports() {
        return dao.getAllReports().size();
    }

    public int getTotalAffectedAnimals() {
        int total = 0;
        for (DiseaseReport r : dao.getAllReports()) total += r.getAffectedCount();
        return total;
    }

    public int getRegionsRepresented() {
        Set<Integer> regions = new HashSet<>();
        for (DiseaseReport r : dao.getAllReports()) regions.add(r.getRegionId());
        return regions.size();
    }

    public Map<String, Integer> getReportsByRegion() {
        return toMap(dao.getReportCountsByRegion());
    }

    /** Keys look like "2026-11" and come back in date order. */
    public Map<String, Integer> getReportsByMonth() {
        return toMap(dao.getReportCountsByMonth());
    }

    public Map<String, Integer> getReportsByLivestockType() {
        return toMap(dao.getCountsByLivestockType());
    }

    /** Counted here because the DAO has no by-disease query. */
    public Map<String, Integer> getReportsByDisease() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (DiseaseReport r : dao.getAllReports()) {
            String name = r.getDiseaseName() != null ? r.getDiseaseName()
                                                      : "disease #" + r.getDiseaseId();
            counts.merge(name, 1, Integer::sum);
        }
        return sortedByCountDescending(counts);
    }

    // The DAO returns rows of {label, count}; turn them into an ordered Map.
    static Map<String, Integer> toMap(List<Object[]> rows) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (Object[] row : rows) {
            map.put(String.valueOf(row[0]), ((Number) row[1]).intValue());
        }
        return map;
    }

    private static Map<String, Integer> sortedByCountDescending(Map<String, Integer> in) {
        Map<String, Integer> out = new LinkedHashMap<>();
        in.entrySet().stream()
          .sorted((a, b) -> b.getValue() - a.getValue())
          .forEach(e -> out.put(e.getKey(), e.getValue()));
        return out;
    }
}
