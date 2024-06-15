package com.hits.open.world.client.keycloak;

public interface RoleClient {
    void assignRole(String userId, String roleName);

    void removeRole(String userId, String roleName);
}
