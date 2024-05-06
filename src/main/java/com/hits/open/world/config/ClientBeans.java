package com.hits.open.world.config;

import com.hits.open.world.keycloak.KeycloakRoleClient;
import com.hits.open.world.keycloak.KeycloakUserClient;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientBeans {
    @Bean
    public Keycloak keycloak(
            @Value("${hits-project.services.users.uri}") String catalogueBaseUri,
            @Value("${spring.security.oauth2.client.registration.keycloak.client-id}") String registrationId,
            @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}") String secret,
            @Value("${hits-project.users-realm}") String realm) {
        return KeycloakBuilder.builder()
                .serverUrl(catalogueBaseUri)
                .clientId(registrationId)
                .clientSecret(secret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .realm(realm)
                .build();
    }

    @Bean
    public KeycloakUserClient usersRestClient(
            Keycloak keycloak,
            @Value("${hits-project.users-realm}") String realm) {
        return new KeycloakUserClient(
                keycloak,
                realm
        );
    }

    @Bean
    public KeycloakRoleClient rolesRestClient(
            Keycloak keycloak,
            @Value("${hits-project.users-realm}") String realm) {
        return new KeycloakRoleClient(
                keycloak,
                realm
        );
    }
}
