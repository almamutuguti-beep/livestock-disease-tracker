package db;

import model.Region;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegionDAO {

    public List<Region> getAllRegions() {
        List<Region> list = new ArrayList<>();
        String sql = "SELECT * FROM regions ORDER BY region_name";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Region(
                    rs.getInt("region_id"),
                    rs.getString("region_name"),
                    rs.getString("county_name")
                ));
            }
        } catch (SQLException e) {
            System.err.println("getAllRegions error: " + e.getMessage());
        }
        return list;
    }
}