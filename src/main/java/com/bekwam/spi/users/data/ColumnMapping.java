package com.bekwam.spi.users.data;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ColumnMapping {

    /** Parst "spalte=keycloakFeld" Zeilen aus dem Admin-UI-Textfeld */
    public static Map<String, String> parse(String raw) {
        Map<String, String> mapping = new LinkedHashMap<>();
        if (raw == null) return mapping;
        for (String line : raw.split("\\r?\\n")) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            String[] parts = line.split("=", 2);
            if (parts.length == 2) {
                mapping.put(parts[0].trim(), parts[1].trim());
            }
        }
        return mapping;
    }

    /** Baut ein User-Objekt aus der aktuellen ResultSet-Zeile anhand des Mappings */
    public static User mapRow(ResultSet rs, Map<String, String> mapping) throws SQLException {
        Map<String, String> values = new LinkedHashMap<>();
        ResultSetMetaData md = rs.getMetaData();
        for (int i = 1; i <= md.getColumnCount(); i++) {
            String colLabel = md.getColumnLabel(i);
            String target = mapping.get(colLabel);
            if (target != null) {
                values.put(target, rs.getString(i));
            }
        }
        return new User(values);
    }
}
