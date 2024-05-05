package com.hits.open.world.core.keycloak;


import com.hits.open.world.core.UserEntity;
import com.hits.open.world.public_interface.UpdateUserDto;

import java.util.List;
import java.util.Optional;

public interface UserClient {
    String registerUser(UserEntity entity);

    void deleteUser(String oauthId);

    void updateUser(UpdateUserDto dto, String oauthId);

    Optional<UserEntity> getUser(String oauthId);

    Optional<UserEntity> getUserByUsername(String username);

    Optional<UserEntity> getUserByEmail(String email);

    List<UserEntity> getUsersByName(String userName);
}
