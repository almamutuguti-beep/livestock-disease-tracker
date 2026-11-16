package db;

import model.DiseaseReport;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiseaseReportDAO {

    // ADD
    public boolean addReport(DiseaseReport r) {
        String sql = "INSERT INTO disease_reports "
                   + "(disease_id, region_id, livestock_type, report_date, "
                   + " affected_count, report_status, notes) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getDiseaseId());
            ps.setInt(2, r.getRegionId());
            ps.setString(3, r.getLivestockType());
            ps.setDate(4, Date.valueOf(r.getReportDate()));
            ps.setInt(5, r.getAffectedCount());
            ps.setString(6, r.getReportStatus());
            ps.setString(7, r.getNotes());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("addReport error: " + e.getMessage());
            return false;
        }
    }

    // GET ALL
    public List<DiseaseReport> getAllReports() {
        List<DiseaseReport> list = new ArrayList<>();
        String sql = "SELECT dr.*, d.disease_name, r.region_name "
                   + "FROM disease_reports dr "
                   + "JOIN diseases d ON dr.disease_id = d.disease_id "
                   + "JOIN regions  r ON dr.region_id  = r.region_id "
                   + "ORDER BY dr.report_date DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement  st   = conn.createStatement();
             ResultSet  rs   = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("getAllReports error: " + e.getMessage());
        }
        return list;
    }

    // UPDATE
    public boolean updateReport(DiseaseReport r) {
        String sql = "UPDATE disease_reports SET "
                   + "disease_id=?, region_id=?, livestock_type=?, "
                   + "report_date=?, affected_count=?, report_status=?, notes=? "
                   + "WHERE report_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getDiseaseId());
            ps.setInt(2, r.getRegionId());
            ps.setString(3, r.getLivestockType());
            ps.setDate(4, Date.valueOf(r.getReportDate()));
            ps.setInt(5, r.getAffectedCount());
            ps.setString(6, r.getReportStatus());
            ps.setString(7, r.getNotes());
            ps.setInt(8, r.getReportId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateReport error: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public boolean deleteReport(int reportId) {
        String sql = "DELETE FROM disease_reports WHERE report_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reportId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("deleteReport error: " + e.getMessage());
            return false;
        }
    }

    // DASHBOARD - by region
    public List<Object[]> getReportCountsByRegion() {
        List<Object[]> results = new ArrayList<>();
        String sql = "SELECT r.region_name, COUNT(*) AS report_count "
                   + "FROM disease_reports dr "
                   + "JOIN regions r ON dr.region_id = r.region_id "
                   + "GROUP BY r.region_name ORDER BY report_count DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                results.add(new Object[]{rs.getString("region_name"),
                                         rs.getInt("report_count")});
            }
        } catch (SQLException e) {
            System.err.println("getReportCountsByRegion error: " + e.getMessage());
        }
        return results;
    }

    // DASHBOARD - by month
    public List<Object[]> getReportCountsByMonth() {
        List<Object[]> results = new ArrayList<>();
        String sql = "SELECT DATE_FORMAT(report_date, '%Y-%m') AS month, "
                   + "COUNT(*) AS report_count "
                   + "FROM disease_reports "
                   + "GROUP BY month ORDER BY month";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                results.add(new Object[]{rs.getString("month"),
                                         rs.getInt("report_count")});
            }
        } catch (SQLException e) {
            System.err.println("getReportCountsByMonth error: " + e.getMessage());
        }
        return results;
    }

    // DASHBOARD - by livestock type
    public List<Object[]> getCountsByLivestockType() {
        List<Object[]> results = new ArrayList<>();
        String sql = "SELECT livestock_type, COUNT(*) AS report_count "
                   + "FROM disease_reports "
                   + "GROUP BY livestock_type ORDER BY report_count DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                results.add(new Object[]{rs.getString("livestock_type"),
                                         rs.getInt("report_count")});
            }
        } catch (SQLException e) {
            System.err.println("getCountsByLivestockType error: " + e.getMessage());
        }
        return results;
    }

    // HELPER
    private DiseaseReport mapRow(ResultSet rs) throws SQLException {
        DiseaseReport r = new DiseaseReport(
            rs.getInt("report_id"),
            rs.getInt("disease_id"),
            rs.getInt("region_id"),
            rs.getString("livestock_type"),
            rs.getDate("report_date").toLocalDate(),
            rs.getInt("affected_count"),
            rs.getString("report_status")
        );
        r.setNotes(rs.getString("notes"));
        return r;
    }
}