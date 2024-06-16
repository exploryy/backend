package com.hits.open.world.public_interface.friend;

import com.hits.open.world.public_interface.user.ProfileDto;

import java.util.List;

public record AllFriendDto(
        List<ProfileDto> favoriteFriends,
        List<ProfileDto> friends
) {
}
