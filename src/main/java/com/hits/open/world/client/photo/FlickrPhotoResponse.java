package com.hits.open.world.client.photo;

import java.util.List;

public record FlickrPhotoResponse(
        Photos photos
) {
    public record Photos(
            int page,
            int pages,
            int perpage,
            int total,
            List<Photo> photo
    ) {
        public record Photo(
                String id,
                String owner,
                String secret,
                String server,
                int farm,
                String title,
                int ispublic,
                int isfriend,
                int isfamily
        ) {
            public String getUrl() {
                return String.format("https://farm%s.staticflickr.com/%s/%s_%s.jpg", farm, server, id, secret);
            }
        }
    }
}
