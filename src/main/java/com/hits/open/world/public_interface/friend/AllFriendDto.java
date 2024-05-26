package com.hits.open.world.public_interface.friend;

import java.util.List;

public record AllFriendDto(
        List<FriendDto> favoriteFriends,
        List<FriendDto> friends
) {
}
