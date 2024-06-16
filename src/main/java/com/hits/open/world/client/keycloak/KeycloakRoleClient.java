package com.hits.open.world.client.keycloak;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;

import java.util.Collections;

@RequiredArgsConstructor
public class KeycloakRoleClient implements RoleClient {
    private final Keycloak keycloak;
    private final String realm;

    @Override
    public void assignRole(String userId, String roleName) {
        UserResource userResource = keycloak.realm(realm).users().get(userId);
        RolesResource rolesResource = getRolesResource();
        RoleRepresentation representation = rolesResource.get(roleName).toRepresentation();
        userResource.roles().realmLevel().add(Collections.singletonList(representation));
    }

    @Override
    public void removeRole(String userId, String roleName) {
        UserResource userResource = keycloak.realm(realm).users().get(userId);
        RolesResource rolesResource = getRolesResource();
        RoleRepresentation representation = rolesResource.get(roleName).toRepresentation();
        userResource.roles().realmLevel().remove(Collections.singletonList(representation));
    }

    private RolesResource getRolesResource() {
        return keycloak.realm(realm).roles();
    }
}
