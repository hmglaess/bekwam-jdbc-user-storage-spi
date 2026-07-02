package com.bekwam.spi.users.data;

import org.jboss.logging.Logger;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class UserDAOImpl implements UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAOImpl.class);

    private final String allUsersSQL;
    private final String searchUsersSQL;
    private final Map<String, String> columnMapping;

    public UserDAOImpl(String allUsersSQL, String searchUsersSQL, Map<String, String> columnMapping) {
        this.allUsersSQL = allUsersSQL;
        this.searchUsersSQL = searchUsersSQL;
        this.columnMapping = columnMapping;
    }

    @Override
    public Optional<User> findUserByUsername(DataSource ds, String sql, String username) {
        LOGGER.trace("findUserByUsrCode sql= " + sql + "; username=" + username);
        try (
                Connection c = ds.getConnection();
                PreparedStatement preparedStatement = c.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return Optional.of(ColumnMapping.mapRow(rs, columnMapping));
            }
        } catch (SQLException exc) {
            LOGGER.error("error running users query '" + sql + "' for username=" + username, exc);
        }
        return Optional.empty();
    }

    public Set<Role> findRolesByUsername(DataSource ds, String sql, String username) {
        Set<Role> roles = new HashSet<>();
        try (
                Connection c = ds.getConnection();
                PreparedStatement preparedStatement = c.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, username);
            var rs = preparedStatement.executeQuery();
            while (rs.next()) {
                roles.add(new Role(rs.getString(1)));
            }
        } catch (SQLException exc) {
            LOGGER.error("error running roles query '" + sql + "' for username=" + username, exc);
        }
        return roles;
    }

    @Override
    public List<User> findUsers(DataSource ds, int first, int max, String criteria) {
        var users = new ArrayList<User>();
        if (criteria == null || criteria.isEmpty()) {
            try (
                    var c = ds.getConnection();
                    var stmt = c.createStatement()
            ) {
                var rs = stmt.executeQuery(allUsersSQL);
                unpackResults(rs, first, max, users);
            } catch (SQLException exc) {
                LOGGER.error("error running all users query '" + allUsersSQL, exc);
            }
        } else {
            try (
                    Connection c = ds.getConnection();
                    PreparedStatement preparedStatement = c.prepareStatement(searchUsersSQL)
            ) {
                preparedStatement.setString(1, criteria);
                preparedStatement.setString(2, criteria);
                preparedStatement.setString(3, criteria);
                var rs = preparedStatement.executeQuery();
                unpackResults(rs, first, max, users);
            } catch (SQLException exc) {
                LOGGER.error("error running search users query '" + searchUsersSQL, exc);
            }
        }
        return users;
    }

    void unpackResults(ResultSet rs, int first, int max, List<User> users) throws SQLException {
        int pos = 0;
        while (rs.next() && pos < (first + max)) {
            if (pos >= first) {
                users.add(ColumnMapping.mapRow(rs, columnMapping));
            }
            pos++;
        }
    }
}
