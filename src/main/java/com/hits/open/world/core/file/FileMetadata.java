package com.hits.open.world.core.file;

import java.util.HashMap;
import java.util.Map;

public record FileMetadata(
        String fileName,
        String contentType,
        Long contentLength
) {
    public Map<String, String> getMapMetadata() {
        var map = new HashMap<String, String>(2);
        map.put(MetadataConst.CONTENT_TYPE.name, contentType);
        map.put(MetadataConst.CONTENT_LENGTH.name, String.valueOf(contentLength));
        return map;
    }
}
