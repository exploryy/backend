package com.hits.open.world.rest;

import com.hits.open.world.public_interface.CreateUserDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@SecurityRequirement(name = "oauth2")
@Tag(name = "User")
public class UserController {
    private final UserService userService;

    @PostMapping(path = "/user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
}
