package com.bekwam.spi.users.data;

import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

import java.util.*;
import java.util.stream.Collectors;

public class UserAndRoles {

    private static final Logger LOGGER = Logger.getLogger(UserAndRoles.class);

    public static UserModel userModel(KeycloakSession session, RealmModel realmModel, ComponentModel model, User u) {
        return new AbstractUserAdapterFederatedStorage(session, realmModel, model) {
            @Override
            public String getUsername() { return u.username(); }

            @Override
            public void setUsername(String username) {
                throw new UnsupportedOperationException("jdbc users are read-only");
            }

            @Override
            public String getFirstName() { return u.firstName(); }

            @Override
            public String getLastName() { return u.lastName(); }

            @Override
            public String getEmail() { return u.email(); }

            @Override
            public String getFirstAttribute(String name) {
                String v = u.customAttributes().get(name);
                return v != null ? v : super.getFirstAttribute(name);
            }

            @Override
            public Map<String, List<String>> getAttributes() {
                Map<String, List<String>> attrs = new HashMap<>(super.getAttributes());
                u.customAttributes().forEach((k, v) -> attrs.put(k, Collections.singletonList(v)));
                return attrs;
            }
        };
    }

    public static UserModel userModel(KeycloakSession session, RealmModel realmModel, ComponentModel model, User u, Set<Role> roles) {
        return new AbstractUserAdapterFederatedStorage(session, realmModel, model) {
            @Override
            public String getUsername() { return u.username(); }

            @Override
            public void setUsername(String s) {
                throw new UnsupportedOperationException("jdbc read-only for now");
            }

            @Override
            public String getFirstName() { return u.firstName(); }

            @Override
            public String getLastName() { return u.lastName(); }

            @Override
            public String getEmail() { return u.email(); }

            @Override
            public String getFirstAttribute(String name) {
                String v = u.customAttributes().get(name);
                return v != null ? v : super.getFirstAttribute(name);
            }

            @Override
            public Map<String, List<String>> getAttributes() {
                Map<String, List<String>> attrs = new HashMap<>(super.getAttributes());
                u.customAttributes().forEach((k, v) -> attrs.put(k, Collections.singletonList(v)));
                return attrs;
            }

            @Override
            protected Set<RoleModel> getRoleMappingsInternal() {
                Set<String> roleNames = roles.stream().map(Role::name).collect(Collectors.toSet());
                return realmModel.getRolesStream()
                        .filter(rm -> roleNames.contains(rm.getName()))
                        .collect(Collectors.toSet());
            }
        };
    }
}
