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

    public static class Guidance {
        private final String name;
        private final String description;
        private final String action;

        public Guidance(String name, String description, String action) {
            this.name = name;
            this.description = description;
            this.action = action;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getAction() { return action; }

        @Override
        public String toString() { return name; }
    }

    public static List<Item> getDiseases() {
        return load("SELECT disease_id, disease_name FROM diseases ORDER BY disease_name");
    }

    public static List<Item> getRegions() {
        return load("SELECT region_id, region_name FROM regions ORDER BY region_name");
    }

    public static List<Guidance> getGuidance() {
        List<Guidance> list = new ArrayList<>();
        String sql = "SELECT disease_name, description, recommended_action FROM diseases ORDER BY disease_name";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Guidance(rs.getString(1), rs.getString(2), rs.getString(3)));
            }
        } catch (SQLException e) {
            System.err.println("ReferenceData guidance error: " + e.getMessage());
        }
        return list;
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