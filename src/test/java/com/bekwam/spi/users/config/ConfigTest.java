package com.bekwam.spi.users.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {

    @Test
    void testEquals() {
        var c1 = new Config("url", "u", "p", "usersSQL", "rolesSQL",
                "1", "10", true, "upper", "5", "Postgres",
                "allUsersSQL", "searchUsersSQL", "validationSQL",
                "username=username\npassword=password");
        var c2 = new Config("url", "u", "p", "usersSQL", "rolesSQL",
                "1", "10", true, "upper", "5", "Postgres",
                "allUsersSQL", "searchUsersSQL", "validationSQL",
                "username=username\npassword=password");
        var c3 = new Config("other-url", "u", "p", "usersSQL", "rolesSQL",
                "1", "10", true, "upper", "5", "Postgres",
                "allUsersSQL", "searchUsersSQL", "validationSQL",
                "username=username\npassword=password");
        var c4 = new Config("url", "u", "p", "usersSQL", "rolesSQL",
                "1", "10", true, "upper", "5", "Postgres",
                "allUsersSQL", "searchUsersSQL", "validationSQL",
                "other=mapping");

        assertEquals(c1, c2, "gleiche Configs sollten equals sein");
        assertEquals(c1.hashCode(), c2.hashCode(), "gleiche Configs sollten gleichen hash haben");
        assertNotEquals(c1, c3, "unterschiedliche URL sollte not equals sein");
        assertNotEquals(c1, c4, "unterschiedliches columnMapping sollte not equals sein");
    }

    @Test
    void testColumnMapping() {
        var c = new Config("url", "u", "p", "usersSQL", "rolesSQL",
                "1", "10", true, "upper", "5", "Postgres",
                "allUsersSQL", "searchUsersSQL", "validationSQL",
                "col1=firstName\ncol2=email");

        assertEquals("col1=firstName\ncol2=email", c.getColumnMapping());
    }

    @Test
    void testGetters() {
        var c = new Config("url", "u", "p", "usersSQL", "rolesSQL",
                "2", "20", false, "lower", "30", "Oracle",
                "allUsersSQL", "searchUsersSQL", "validationSQL",
                "username=username");

        assertEquals("url", c.getConnectionURL());
        assertEquals("u", c.getUsername());
        assertEquals("p", c.getPassword());
        assertEquals("usersSQL", c.getUsersSQL());
        assertEquals("rolesSQL", c.getRolesSQL());
        assertEquals(2, c.getMinSizeAsInt());
        assertEquals(20, c.getMaxSizeAsInt());
        assertFalse(c.isMetricsEnabled());
        assertEquals("lower", c.getUsernameCase());
        assertEquals(30L, c.getValidationTimeoutAsLong());
        assertEquals("Oracle", c.getDbVendor());
        assertEquals("allUsersSQL", c.getAllUsersSQL());
        assertEquals("searchUsersSQL", c.getSearchUsersSQL());
        assertEquals("validationSQL", c.getValidationSQL());
        assertEquals("username=username", c.getColumnMapping());
    }
}
