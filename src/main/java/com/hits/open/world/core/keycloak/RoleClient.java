package com.hits.open.world.core.keycloak;

public interface RoleClient {
    void assignRole(String userId, String roleName);

    void removeRole(String userId, String roleName);
}
