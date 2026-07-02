package com.bekwam.spi.users.data;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ColumnMappingTest {

    @Test
    void testParseNull() {
        var result = ColumnMapping.parse(null);
        assertTrue(result.isEmpty(), "null sollte leere Map zurückgeben");
    }

    @Test
    void testParseEmptyString() {
        var result = ColumnMapping.parse("");
        assertTrue(result.isEmpty(), "leerer String sollte leere Map zurückgeben");
    }

    @Test
    void testParseSingleLine() {
        var result = ColumnMapping.parse("username=username");
        assertEquals(1, result.size());
        assertEquals("username", result.get("username"));
    }

    @Test
    void testParseMultipleLines() {
        var result = ColumnMapping.parse(
                "username=username\n" +
                "password=password\n" +
                "name=firstName\n" +
                "email=email"
        );
        assertEquals(4, result.size());
        assertEquals("username", result.get("username"));
        assertEquals("password", result.get("password"));
        assertEquals("firstName", result.get("name"));
        assertEquals("email", result.get("email"));
    }

    @Test
    void testParseWithCommentsAndBlanks() {
        var result = ColumnMapping.parse(
                "# das ist ein Kommentar\n" +
                "\n" +
                "username=username\n" +
                "   \n" +
                "email=email"
        );
        assertEquals(2, result.size());
    }

    @Test
    void testParseIgnoresInvalidLines() {
        var result = ColumnMapping.parse("username=username\ninvalidline\nno equals here");
        assertEquals(1, result.size());
    }

    @Test
    void testMapRow() throws Exception {
        var mapping = ColumnMapping.parse("username=username\nname=firstName\nemail=email");

        var rs = mock(ResultSet.class);
        var md = mock(ResultSetMetaData.class);

        when(rs.getMetaData()).thenReturn(md);
        when(md.getColumnCount()).thenReturn(3);
        when(md.getColumnLabel(1)).thenReturn("username");
        when(md.getColumnLabel(2)).thenReturn("name");
        when(md.getColumnLabel(3)).thenReturn("email");

        when(rs.getString(1)).thenReturn("carl");
        when(rs.getString(2)).thenReturn("Carl");
        when(rs.getString(3)).thenReturn("carl@bekwam.com");

        var user = ColumnMapping.mapRow(rs, mapping);

        assertNotNull(user);
        assertEquals("carl", user.username());
        assertEquals("Carl", user.firstName());
        assertEquals("carl@bekwam.com", user.email());
        assertNull(user.password());
        assertNull(user.lastName());
    }

    @Test
    void testMapRowWithOnlyMappedColumns() throws Exception {
        var mapping = ColumnMapping.parse("display_name=firstName");

        var rs = mock(ResultSet.class);
        var md = mock(ResultSetMetaData.class);

        when(rs.getMetaData()).thenReturn(md);
        when(md.getColumnCount()).thenReturn(2);
        when(md.getColumnLabel(1)).thenReturn("display_name");
        when(md.getColumnLabel(2)).thenReturn("ignored_col");

        when(rs.getString(1)).thenReturn("Carl");
        when(rs.getString(2)).thenReturn("irrelevant");

        var user = ColumnMapping.mapRow(rs, mapping);

        assertEquals("Carl", user.firstName());
        assertNull(user.username());
        assertNull(user.email());
    }

    @Test
    void testUserCustomAttributes() {
        var values = new java.util.LinkedHashMap<String, String>();
        values.put("username", "carl");
        values.put("password", "hashed");
        values.put("firstName", "Carl");
        values.put("lastName", "Walker");
        values.put("email", "carl@bekwam.com");
        values.put("department", "IT");
        values.put("title", "Developer");

        var user = new User(values);

        assertEquals("carl", user.username());
        assertEquals("hashed", user.password());
        assertEquals("Carl", user.firstName());
        assertEquals("Walker", user.lastName());
        assertEquals("carl@bekwam.com", user.email());

        var custom = user.customAttributes();
        assertEquals(2, custom.size());
        assertEquals("IT", custom.get("department"));
        assertEquals("Developer", custom.get("title"));
        assertNull(custom.get("username"));
    }
}
