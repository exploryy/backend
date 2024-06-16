package com.hits.open.world.core.friend;

import com.hits.open.world.client.photo.PhotoClient;
import com.hits.open.world.core.poi.PoiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@SpringJUnitConfig(classes = {PoiImportConfig.class})
class PoiImportTest {
    @Autowired
    private PoiService poiService;
    @Autowired
    private PhotoClient photoClient;

    @Test
    void importPoiData() {
        poiService.tryLoadPoiData("Томск");
        assertTrue(poiService.getCities().contains("Томск"));
        System.out.println(poiService.getRandomPoiInCity("Томск"));
    }

    @Test
    void downloadPhotos() {
        var photos = photoClient.getPhotosByCoordinates(56.4697591, 84.9459139);
        for (var photo : photos) {
            var multipart = downloadImage(photo);
            try {
                multipart.transferTo(new File("src/test/resources/" + multipart.getName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public MultipartFile downloadImage(String url) {
        byte[] imageBytes = new RestTemplate().getForObject(url, byte[].class);
        if (imageBytes == null) {
            throw new RuntimeException("Failed to download file from URL: " + url);
        }

        String fileName = url.substring(url.lastIndexOf('/') + 1);
        return new MultipartFile() {
            @Override
            public String getName() {
                return fileName;
            }

            @Override
            public String getOriginalFilename() {
                return fileName;
            }

            @Override
            public String getContentType() {
                return "image/jpeg";
            }

            @Override
            public boolean isEmpty() {
                return imageBytes.length == 0;
            }

            @Override
            public long getSize() {
                return imageBytes.length;
            }

            @Override
            public byte[] getBytes() {
                return imageBytes;
            }

            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream(imageBytes);
            }

            @Override
            public void transferTo(File dest) throws IOException {
                Files.write(dest.toPath(), imageBytes);
            }
        };
    }
}
