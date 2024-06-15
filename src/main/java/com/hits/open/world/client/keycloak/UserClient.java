package com.hits.open.world.client.keycloak;


import com.hits.open.world.core.user.UserEntity;
import com.hits.open.world.public_interface.user.UpdateUserDto;

import java.util.List;
import java.util.Optional;

public interface UserClient {
    String registerUser(UserEntity entity);

    void deleteUser(String oauthId);

    void updateUser(UpdateUserDto dto);

    Optional<UserEntity> getUser(String oauthId);

    Optional<UserEntity> getUserByUsername(String username);

    Optional<UserEntity> getUserByEmail(String email);
    List<UserEntity> getUsersByUsername(String username);
    List<UserEntity> getAllUsers();
}
