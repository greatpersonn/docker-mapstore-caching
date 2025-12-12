package com.example;

import com.hazelcast.map.MapStore;

import java.sql.*;
import java.util.*;

public class CounterMapStore implements MapStore<Integer, Long> {

    private final String url = "jdbc:postgresql://localhost:5432/countersdb";
    private final String user = "admin";
    private final String password = "admin";

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public Long load(Integer key) {
        try (Connection conn = connect()) {
            PreparedStatement st = conn.prepareStatement(
              "SELECT value FROM counters WHERE id = ?"
            );
            st.setInt(1, key);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return rs.getLong("value");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    @Override
    public Map<Integer, Long> loadAll(Collection<Integer> keys) {
        Map<Integer, Long> map = new HashMap<>();
        keys.forEach(k -> map.put(k, load(k)));
        return map;
    }

    @Override
    public Iterable<Integer> loadAllKeys() {
        return null;
    }

    @Override
    public void store(Integer key, Long value) {
        try (Connection conn = connect()) {
            PreparedStatement st = conn.prepareStatement(
              "INSERT INTO counters (id, value) VALUES (?, ?) " +
              "ON CONFLICT (id) DO UPDATE SET value = EXCLUDED.value"
            );
            st.setInt(1, key);
            st.setLong(2, value);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void storeAll(Map<Integer, Long> map) {
        map.forEach(this::store);
    }

    @Override
    public void delete(Integer key) {}

    @Override
    public void deleteAll(Collection<Integer> keys) {}
}
