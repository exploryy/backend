package com.hits.open.world.core.user;

import com.hits.open.world.keycloak.RoleClient;
import com.hits.open.world.keycloak.UserClient;
import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import com.hits.open.world.public_interface.user.CreateUserDto;
import com.hits.open.world.public_interface.user.ProfileDto;
import com.hits.open.world.public_interface.user.UpdateUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserClient userClient;
    private final RoleClient roleClient;

    public String createUser(CreateUserDto dto) {
        checkUserWithUsernameExists(dto.username());
        checkUserWithEmailExists(dto.email());

        var userEntity = new UserEntity(
                null,
                dto.username(),
                dto.email(),
                dto.password()
        );
        var oauthId = userClient.registerUser(userEntity);
        roleClient.assignRole(oauthId, "ROLE_USER");
        return oauthId;
    }

    public ProfileDto getProfile(String userId) {
        var user = userClient.getUser(userId)
                .orElseThrow(() -> new ExceptionInApplication("User with this id does not exist", ExceptionType.NOT_FOUND));
        return new ProfileDto(
                user.id(),
                user.username(),
                user.email()
        );
    }

    public void updateUser(UpdateUserDto dto) {
        if (dto.email().isPresent()) {
            checkUserWithEmailExists(dto.email().get());
        }

        if (dto.username().isPresent()) {
            checkUserWithUsernameExists(dto.username().get());
        }

        userClient.getUser(dto.userId())
                .orElseThrow(() -> new ExceptionInApplication("User with this id does not exist", ExceptionType.NOT_FOUND));
        userClient.updateUser(dto);
    }

    private void checkUserWithUsernameExists(String username) {
        if (userClient.getUserByUsername(username).isPresent()) {
            throw new ExceptionInApplication("User with this username does not exist", ExceptionType.NOT_FOUND);
        }
    }

    private void checkUserWithEmailExists(String email) {
        if (userClient.getUserByEmail(email).isPresent()) {
            throw new ExceptionInApplication("User with this email does not exist", ExceptionType.NOT_FOUND);
        }
    }
}
