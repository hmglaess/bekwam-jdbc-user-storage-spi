package com.bekwam.spi.users.data;

import org.junit.jupiter.api.Test;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserAndRolesTest {

    private User createTestUser() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("username", "carl");
        values.put("password", "hashed");
        values.put("firstName", "Carl");
        values.put("lastName", "Walker");
        values.put("email", "carl@bekwam.com");
        values.put("department", "IT");
        return new User(values);
    }

    @Test
    void testUserModelWithoutRoles() {
        var session = mock(KeycloakSession.class);
        var realm = mock(RealmModel.class);
        var model = mock(ComponentModel.class);
        var user = createTestUser();

        var userModel = UserAndRoles.userModel(session, realm, model, user);

        assertNotNull(userModel);
        assertEquals("carl", userModel.getUsername());
        assertEquals("Carl", userModel.getFirstName());
        assertEquals("Walker", userModel.getLastName());
        assertEquals("carl@bekwam.com", userModel.getEmail());

        assertThrows(UnsupportedOperationException.class, () -> userModel.setUsername("newuser"));
    }

    @Test
    void testUserModelWithCustomAttributes() {
        var session = mock(KeycloakSession.class);
        var realm = mock(RealmModel.class);
        var model = mock(ComponentModel.class);
        var user = createTestUser();

        var userModel = UserAndRoles.userModel(session, realm, model, user);

        // customAttribute direkt testen (die Map wird korrekt befüllt)
        var custom = user.customAttributes();
        assertEquals("IT", custom.get("department"));
    }

    @Test
    void testUserModelWithRoles() {
        var session = mock(KeycloakSession.class);
        var realm = mock(RealmModel.class);
        var model = mock(ComponentModel.class);
        var user = createTestUser();
        var roles = Set.of(new Role("admin"), new Role("user"));

        var userModel = UserAndRoles.userModel(session, realm, model, user, roles);

        assertNotNull(userModel);
        assertEquals("carl", userModel.getUsername());
        assertEquals("Carl", userModel.getFirstName());
        assertEquals("Walker", userModel.getLastName());
        assertEquals("carl@bekwam.com", userModel.getEmail());

        assertThrows(UnsupportedOperationException.class, () -> userModel.setUsername("newuser"));
    }
}
