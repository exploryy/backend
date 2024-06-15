package com.hits.open.world.core.user;

import com.hits.open.world.core.file.FileMetadata;
import com.hits.open.world.core.file.FileStorageService;
import com.hits.open.world.core.money.MoneyRepository;
import com.hits.open.world.client.keycloak.RoleClient;
import com.hits.open.world.client.keycloak.UserClient;
import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import com.hits.open.world.public_interface.file.UploadFileDto;
import com.hits.open.world.public_interface.user.CreateUserDto;
import com.hits.open.world.public_interface.user.ProfileDto;
import com.hits.open.world.public_interface.user.UpdateUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserClient userClient;
    private final RoleClient roleClient;
    private final FileStorageService fileStorageService;
    private final MoneyRepository moneyRepository;

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
        moneyRepository.initializeMoney(oauthId);
        return oauthId;
    }

    public ProfileDto getProfile(String userId) {
        var user = userClient.getUser(userId)
                .orElseThrow(() -> new ExceptionInApplication("User with this id does not exist", ExceptionType.NOT_FOUND));

        return mapUserToProfileDto(user);
    }

    public void updateUser(UpdateUserDto dto) {
        dto.email().ifPresent(this::checkUserWithEmailExists);
        dto.username().ifPresent(this::checkUserWithUsernameExists);

        var user = userClient.getUser(dto.userId())
                .orElseThrow(() -> new ExceptionInApplication("User with this id does not exist", ExceptionType.NOT_FOUND));

        dto.avatar().ifPresent(avatar -> fileStorageService.deleteFile(user.getPhotoName())
                .then(saveAvatar(user, avatar))
                .subscribe());
        userClient.updateUser(dto);
    }

    public List<ProfileDto> getUsers(Optional<String> username) {
        return username.map(string -> userClient.getUsersByUsername(string)
                .parallelStream()
                .map(this::mapUserToProfileDto)
                .toList()).orElseGet(() -> userClient.getAllUsers()
                .parallelStream()
                .map(this::mapUserToProfileDto)
                .toList());
    }

    private ProfileDto mapUserToProfileDto(UserEntity user) {
        var avatarUrl = fileStorageService.getDownloadLinkByName(user.getPhotoName());
        return new ProfileDto(
                user.id(),
                user.username(),
                user.email(),
                avatarUrl
        );
    }

    private void checkUserWithUsernameExists(String username) {
        if (userClient.getUserByUsername(username).isPresent()) {
            throw new ExceptionInApplication("User with this username exist", ExceptionType.NOT_FOUND);
        }
    }

    private void checkUserWithEmailExists(String email) {
        if (userClient.getUserByEmail(email).isPresent()) {
            throw new ExceptionInApplication("User with this email exist", ExceptionType.NOT_FOUND);
        }
    }

    private Mono<Void> saveAvatar(UserEntity user, MultipartFile avatar) {
        var metadata = new FileMetadata(
                user.getPhotoName(),
                avatar.getContentType(),
                avatar.getSize()
        );
        var fileDto = new UploadFileDto(
                metadata,
                avatar
        );
        return fileStorageService.uploadFile(fileDto);
    }
}
