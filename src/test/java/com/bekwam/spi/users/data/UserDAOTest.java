package com.bekwam.spi.users.data;

import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserDAOTest {

    private final Map<String, String> defaultMapping = ColumnMapping.parse(
            "username=username\npassword=password\nname=firstName\nemail=email"
    );

    @Test
    void testFindUserByUsername() throws Exception {
        var ds = mock(DataSource.class);
        var conn = mock(Connection.class);
        var ps = mock(PreparedStatement.class);
        var rs = mock(ResultSet.class);
        var md = mock(java.sql.ResultSetMetaData.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getMetaData()).thenReturn(md);
        when(md.getColumnCount()).thenReturn(4);
        when(md.getColumnLabel(1)).thenReturn("username");
        when(md.getColumnLabel(2)).thenReturn("password");
        when(md.getColumnLabel(3)).thenReturn("name");
        when(md.getColumnLabel(4)).thenReturn("email");
        when(rs.getString(1)).thenReturn("carl");
        when(rs.getString(2)).thenReturn("hashedpassword");
        when(rs.getString(3)).thenReturn("Carl");
        when(rs.getString(4)).thenReturn("carl@bekwam.com");

        var dao = new UserDAOImpl("allUsersSQL", "searchUsersSQL", defaultMapping);
        var result = dao.findUserByUsername(ds, "SELECT ...", "carl");

        assertTrue(result.isPresent());
        assertEquals("carl", result.get().username());
        assertEquals("hashedpassword", result.get().password());
        assertEquals("Carl", result.get().firstName());
        assertEquals("carl@bekwam.com", result.get().email());

        verify(ps).setString(1, "carl");
    }

    @Test
    void testFindUserByUsernameNotFound() throws Exception {
        var ds = mock(DataSource.class);
        var conn = mock(Connection.class);
        var ps = mock(PreparedStatement.class);
        var rs = mock(ResultSet.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        var dao = new UserDAOImpl("allUsersSQL", "searchUsersSQL", defaultMapping);
        var result = dao.findUserByUsername(ds, "SELECT ...", "unknown");

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindRolesByUsername() throws Exception {
        var ds = mock(DataSource.class);
        var conn = mock(Connection.class);
        var ps = mock(PreparedStatement.class);
        var rs = mock(ResultSet.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(rs.getString(1)).thenReturn("admin").thenReturn("user");

        var dao = new UserDAOImpl("allUsersSQL", "searchUsersSQL", defaultMapping);
        var result = dao.findRolesByUsername(ds, "SELECT ...", "carl");

        assertEquals(2, result.size());
        assertTrue(result.contains(new Role("admin")));
        assertTrue(result.contains(new Role("user")));
    }

    @Test
    void testFindAllUsers() throws Exception {
        var ds = mock(DataSource.class);
        var conn = mock(Connection.class);
        var stmt = mock(Statement.class);
        var rs = mock(ResultSet.class);
        var md = mock(java.sql.ResultSetMetaData.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenReturn(rs);

        when(rs.getMetaData()).thenReturn(md);
        when(md.getColumnCount()).thenReturn(4);
        when(md.getColumnLabel(1)).thenReturn("username");
        when(md.getColumnLabel(2)).thenReturn("password");
        when(md.getColumnLabel(3)).thenReturn("name");
        when(md.getColumnLabel(4)).thenReturn("email");

        when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
        when(rs.getString(1)).thenReturn("user1").thenReturn("user2").thenReturn("user3");
        when(rs.getString(2)).thenReturn("pw1").thenReturn("pw2").thenReturn("pw3");
        when(rs.getString(3)).thenReturn("User One").thenReturn("User Two").thenReturn("User Three");
        when(rs.getString(4)).thenReturn("u1@test.com").thenReturn("u2@test.com").thenReturn("u3@test.com");

        var dao = new UserDAOImpl("SELECT * FROM \"user\"", "searchUsersSQL", defaultMapping);
        var users = dao.findUsers(ds, 0, 10, null);

        assertEquals(3, users.size());
        assertEquals("user1", users.get(0).username());
        assertEquals("user2", users.get(1).username());
        assertEquals("user3", users.get(2).username());
    }

    @Test
    void testFindUsersWithPagination() throws Exception {
        var ds = mock(DataSource.class);
        var conn = mock(Connection.class);
        var stmt = mock(Statement.class);
        var rs = mock(ResultSet.class);
        var md = mock(java.sql.ResultSetMetaData.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenReturn(rs);

        when(rs.getMetaData()).thenReturn(md);
        when(md.getColumnCount()).thenReturn(4);
        when(md.getColumnLabel(1)).thenReturn("username");
        when(md.getColumnLabel(2)).thenReturn("password");
        when(md.getColumnLabel(3)).thenReturn("name");
        when(md.getColumnLabel(4)).thenReturn("email");

        // 3 Users (erwartet: alle ohne limit)
        when(rs.next()).thenReturn(true, true, true, false);
        when(rs.getString(1)).thenReturn("user1", "user2", "user3");
        when(rs.getString(2)).thenReturn("pw1", "pw2", "pw3");
        when(rs.getString(3)).thenReturn("U1", "U2", "U3");
        when(rs.getString(4)).thenReturn("u1@t.com", "u2@t.com", "u3@t.com");

        var dao = new UserDAOImpl("SELECT * FROM \"user\"", "searchUsersSQL", defaultMapping);
        var users = dao.findUsers(ds, 0, 10, null);

        assertEquals(3, users.size());
        assertEquals("user1", users.get(0).username());
        assertEquals("user2", users.get(1).username());
        assertEquals("user3", users.get(2).username());
    }

    @Test
    void testFindUsersWithSearch() throws Exception {
        var ds = mock(DataSource.class);
        var conn = mock(Connection.class);
        var ps = mock(PreparedStatement.class);
        var rs = mock(ResultSet.class);
        var md = mock(java.sql.ResultSetMetaData.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.getMetaData()).thenReturn(md);
        when(md.getColumnCount()).thenReturn(4);
        when(md.getColumnLabel(1)).thenReturn("username");
        when(md.getColumnLabel(2)).thenReturn("password");
        when(md.getColumnLabel(3)).thenReturn("name");
        when(md.getColumnLabel(4)).thenReturn("email");

        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getString(1)).thenReturn("carl");
        when(rs.getString(2)).thenReturn("pw");
        when(rs.getString(3)).thenReturn("Carl");
        when(rs.getString(4)).thenReturn("carl@bekwam.com");

        var dao = new UserDAOImpl("allUsersSQL", "SELECT ... WHERE username LIKE ?", defaultMapping);
        var users = dao.findUsers(ds, 0, 10, "car%");

        assertEquals(1, users.size());
        assertEquals("carl", users.get(0).username());
        verify(ps).setString(1, "car%");
        verify(ps).setString(2, "car%");
        verify(ps).setString(3, "car%");
    }
}
