package com.hits.open.world.core.keycloak;

import com.hits.open.world.core.UserEntity;
import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import com.hits.open.world.public_interface.UpdateUserDto;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class KeycloakUserClient implements UserClient {
    private final Keycloak keycloak;
    private final String realm;

    @Override
    public String registerUser(UserEntity entity) {
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(entity.password());

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(entity.username());
        userRepresentation.setEmail(entity.email());
        userRepresentation.setEmailVerified(false);
        userRepresentation.setEnabled(true);
        userRepresentation.setCredentials(List.of(passwordCred));

        UsersResource usersResource = getUsersResource();

        try (Response response = usersResource.create(userRepresentation)) {
            if (!Objects.equals(201, response.getStatus())) {
                log.error("Error creating user: {}", response.getEntity());
                throw new ExceptionInApplication("Error creating user", ExceptionType.INVALID);
            }

            return CreatedResponseUtil.getCreatedId(response);
        }
    }

    @Override
    public void deleteUser(String oauthId) {
        UsersResource usersResource = getUsersResource();
        try (Response response = usersResource.delete(oauthId)) {
            if (!Objects.equals(204, response.getStatus())) {
                log.error("Error deleting user: {}", response.getEntity());
                throw new ExceptionInApplication("Error deleting user", ExceptionType.INVALID);
            }
        }
    }

    @Override
    public void updateUser(UpdateUserDto dto, String oauthId) {
        UsersResource usersResource = getUsersResource();
        UserRepresentation userRepresentation = usersResource.get(oauthId).toRepresentation();
        if (dto.username().isPresent()) {
            userRepresentation.setUsername(dto.username().get());
        }
        if (dto.email().isPresent()) {
            userRepresentation.setEmail(dto.email().get());
        }
        if (dto.password().isPresent()) {
            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(dto.password().get());
            userRepresentation.setCredentials(List.of(passwordCred));
        }

        usersResource.get(oauthId).update(userRepresentation);
    }

    @Override
    public Optional<UserEntity> getUser(String oauthId) {
        UsersResource usersResource = getUsersResource();
        UserRepresentation userRepresentation = usersResource.get(oauthId).toRepresentation();
        if (userRepresentation == null) {
            return Optional.empty();
        }
        return Optional.of(userRepresentationToEntity(userRepresentation));
    }

    @Override
    public Optional<UserEntity> getUserByUsername(String username) {
        UsersResource usersResource = getUsersResource();
        List<UserRepresentation> userRepresentations = usersResource.searchByUsername(username, true);
        if (userRepresentations.isEmpty()) {
            return Optional.empty();
        }
        UserRepresentation userRepresentation = userRepresentations.getFirst();
        return Optional.of(userRepresentationToEntity(userRepresentation));
    }

    @Override
    public Optional<UserEntity> getUserByEmail(String email) {
        UsersResource usersResource = getUsersResource();
        List<UserRepresentation> userRepresentations = usersResource.searchByEmail(email, true);
        if (userRepresentations.isEmpty()) {
            return Optional.empty();
        }
        UserRepresentation userRepresentation = userRepresentations.getFirst();
        return Optional.of(userRepresentationToEntity(userRepresentation));
    }

    @Override
    public List<UserEntity> getUsersByName(String userName) {
        UsersResource usersResource = getUsersResource();
        List<UserEntity> userRepresentations = List.of();
        try {
            userRepresentations = usersResource.list()
                    .stream().map(this::userRepresentationToEntity)
                    .toList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userRepresentations;
    }

    private UsersResource getUsersResource() {
        RealmResource realm1 = keycloak.realm(realm);
        return realm1.users();
    }

    private UserEntity userRepresentationToEntity(UserRepresentation userRepresentation) {
        return new UserEntity(
                userRepresentation.getId(),
                userRepresentation.getUsername(),
                userRepresentation.getEmail(),
                null
        );
    }

}
