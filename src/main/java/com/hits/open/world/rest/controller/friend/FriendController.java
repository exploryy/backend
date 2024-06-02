package com.hits.open.world.rest.controller.friend;

import com.hits.open.world.core.friend.FriendService;
import com.hits.open.world.public_interface.friend.AllFriendDto;
import com.hits.open.world.public_interface.friend.RequestFriendsDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friend")
@SecurityRequirement(name = "oauth2")
@Tag(name = "Friend")
public class FriendController {
    private final FriendService friendService;

    @PostMapping(path = "/add")
    public void addFriend(@RequestParam("friend_id") String friendId,
                          JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        friendService.addFriendRequest(userId, friendId);
    }

    @DeleteMapping(path = "/remove")
    public void removeFriend(@RequestParam("friend_id") String friendId,
                             JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        friendService.removeFriend(userId, friendId);
    }

    @PostMapping(path = "/accept")
    public void acceptFriend(@RequestParam("friend_id") String friendId,
                             JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        friendService.acceptFriend(userId, friendId);
    }

    @DeleteMapping(path = "/decline")
    public void declineFriend(@RequestParam("friend_id") String friendId,
                              JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        friendService.declineFriend(userId, friendId);
    }

    @PostMapping(path = "/favorite")
    public void favoriteFriend(@RequestParam("friend_id") String friendId,
                               JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        friendService.favoriteFriend(userId, friendId);
    }

    @DeleteMapping(path = "/unfavorite")
    public void unfavoriteFriend(@RequestParam("friend_id") String friendId,
                                 JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        friendService.unfavoriteFriend(userId, friendId);
    }

    @GetMapping(path = "/list")
    public AllFriendDto getFriends(JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        return friendService.getFriends(userId);
    }

    @GetMapping(path = "/requests")
    public RequestFriendsDto getFriendRequests(JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        return friendService.getFriendRequests(userId);
    }
}
