package com.hits.open.world.core.privacy;

import com.hits.open.world.core.privacy.repository.ClientPrivacyEntity;
import com.hits.open.world.core.privacy.repository.ClientPrivacyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientPrivacyService {
    private ClientPrivacyRepository clientPrivacyRepository;

    public void setPrivacy(ClientPrivacyEntity clientPrivacyEntity) {
        clientPrivacyRepository.setPrivacy(clientPrivacyEntity);
    }

    public boolean isPublic(String clientId) {
        return clientPrivacyRepository.isPublic(clientId);
    }
}
