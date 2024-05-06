package com.hits.open.world.keycloak;

public interface RoleClient {
    void assignRole(String userId, String roleName);

    void removeRole(String userId, String roleName);
}
