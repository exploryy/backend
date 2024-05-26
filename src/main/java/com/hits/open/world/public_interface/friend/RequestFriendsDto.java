package com.hits.open.world.public_interface.friend;

import java.util.List;

public record RequestFriendsDto(
        List<FriendDto> my,
        List<FriendDto> other
) {
}
