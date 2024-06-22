package com.hits.open.world.core.privacy;

import com.hits.open.world.core.privacy.repository.ClientPrivacyEntity;
import com.hits.open.world.core.privacy.repository.ClientPrivacyRepository;
import com.hits.open.world.public_interface.privacy.PrivacyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientPrivacyService {
    private final ClientPrivacyRepository clientPrivacyRepository;

    @Transactional
    public void setPrivacy(String clientId, boolean isPrivate) {
        var clientPrivacyEntity = new ClientPrivacyEntity(clientId, isPrivate);
        clientPrivacyRepository.setPrivacy(clientPrivacyEntity);
    }

    @Transactional
    public boolean isPublic(String clientId) {
        return clientPrivacyRepository.isPublic(clientId);
    }

    @Transactional
    public PrivacyDto getPrivacy(String clientId) {
        boolean isPublic = isPublic(clientId);
        return new PrivacyDto(isPublic);
    }
}
