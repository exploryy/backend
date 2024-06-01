package com.hits.open.world.rest.user;

import com.hits.open.world.core.user.UserService;
import com.hits.open.world.public_interface.user.CreateUserDto;
import com.hits.open.world.public_interface.user.ProfileDto;
import com.hits.open.world.public_interface.user.UpdateUserDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "User")
public class UserController {
    private final UserService userService;

    @PostMapping(path = "/register")
    public String createUser(@RequestParam("username") String username,
                             @RequestParam("email") String email,
                             @RequestParam("password") String password) {
        var dto = new CreateUserDto(
                username,
                email,
                password
        );

        return userService.createUser(dto);
    }

    @GetMapping(path = "/profile")
    @SecurityRequirement(name = "oauth2")
    public ProfileDto getProfile(JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        return userService.getProfile(userId);
    }

    @SecurityRequirement(name = "oauth2")
    @PostMapping(path = "/profile")
    public void updateProfile(@RequestParam("username") Optional<String> username,
                              @RequestParam("email") Optional<String> email,
                              @RequestParam("password") Optional<String> password,
                              @RequestParam("avatar") Optional<MultipartFile> avatar,
                              JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        var dto = new UpdateUserDto(
                userId,
                username,
                email,
                password,
                avatar
        );

        userService.updateUser(dto);
    }
}
