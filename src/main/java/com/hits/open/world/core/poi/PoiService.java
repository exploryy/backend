package com.hits.open.world.core.poi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PoiService {
    private final ResourceLoader resourceLoader;
    private final List<PoiEntity> dataList = new ArrayList<>();
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        Resource resource = resourceLoader.getResource("classpath:poi.json");
        try (InputStream inputStream = resource.getInputStream()) {
            dataList.addAll(objectMapper.readValue(inputStream, new TypeReference<List<PoiEntity>>() {}));
        } catch (IOException e) {
            log.error("Error reading file", e);
        }
    }

    public PoiEntity getRandomPoi() {
        return dataList.get((int) (Math.random() * dataList.size()));
    }
}
