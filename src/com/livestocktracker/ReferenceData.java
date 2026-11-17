package com.livestocktracker;

import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReferenceData {

    public static class Item {
        private final int id;
        private final String name;

        public Item(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() { return id; }
        public String getName() { return name; }

        @Override
        public String toString() { return name; }
    }

    public static List<Item> getDiseases() {
        return load("SELECT disease_id, disease_name FROM diseases ORDER BY disease_name");
    }

    public static List<Item> getRegions() {
        return load("SELECT region_id, region_name FROM regions ORDER BY region_name");
    }

    private static List<Item> load(String sql) {
        List<Item> items = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                items.add(new Item(rs.getInt(1), rs.getString(2)));
            }
        } catch (SQLException e) {
            System.err.println("ReferenceData error: " + e.getMessage());
        }
        return items;
    }
}
