package com.hits.open.world.rest.friend;

import com.hits.open.world.core.friend.FriendService;
import com.hits.open.world.public_interface.friend.FriendDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friend")
@SecurityRequirement(name = "oauth2")
@Tag(name = "Friend")
public class FriendController {
    private final FriendService friendService;

    @PostMapping(path = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void addFriend(@RequestParam("friend_id") String friendId,
                          JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        friendService.addFriend(userId, friendId);
    }

    @DeleteMapping(path = "/remove", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void removeFriend(@RequestParam("friend_id") String friendId,
                             JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        friendService.removeFriend(userId, friendId);
    }

    @GetMapping(path = "/list")
    public List<FriendDto> getFriends(JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        return friendService.getFriends(userId);
    }
}
