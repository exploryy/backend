package com.hits.open.world.config;

import com.hits.open.world.client.gpt.YandexGptClient;
import com.hits.open.world.client.keycloak.KeycloakRoleClient;
import com.hits.open.world.client.keycloak.KeycloakUserClient;
import com.hits.open.world.client.map.OpenStreetMapClient;
import com.hits.open.world.client.poi.OverpassTurboPoiClient;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

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

    @Bean
    public YandexGptClient yandexGptClient(
            @Value("${yandexApi.foundationModelsUri}") String baseUrl,
            @Value("${yandexApi.token}") String token,
            @Value("${yandexApi.modelUri}") String modelUri) {
        return new YandexGptClient(WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Api-Key " + token)
                .build(),
                modelUri
        );
    }

    @Bean
    public OpenStreetMapClient openStreetMapClient(
            @Value("${openStreetMap.baseUrl}") String baseUrl,
            @Value("${openStreetMap.carEndpoint}") String carUri,
            @Value("${openStreetMap.footEndpoint}") String footUri,
            @Value("${openStreetMap.bikeEndpoint}") String bicycleUri) {
        return new OpenStreetMapClient(WebClient.builder()
                .baseUrl(baseUrl)
                .build(),
                carUri,
                footUri,
                bicycleUri
        );
    }

    @Bean
    public OverpassTurboPoiClient overpassTurboPoiClient(
            @Value("${overpassTurbo.baseUrl}") String baseUrl) {
        return new OverpassTurboPoiClient(WebClient.builder()
                .baseUrl(baseUrl)
                .build()
        );
    }
}
