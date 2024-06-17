package com.hits.open.world.core.quest;

import com.hits.open.world.client.gpt.GptClient;
import com.hits.open.world.client.map.MapClient;
import com.hits.open.world.client.photo.PhotoClient;
import com.hits.open.world.core.poi.PoiEntity;
import com.hits.open.world.core.poi.PoiService;
import com.hits.open.world.core.quest.repository.entity.generated.GeneratedDistanceQuest;
import com.hits.open.world.core.quest.repository.entity.generated.GeneratedPoint;
import com.hits.open.world.core.quest.repository.entity.generated.GeneratedPointToPointQuest;
import com.hits.open.world.core.quest.repository.entity.quest.DifficultyType;
import com.hits.open.world.core.quest.repository.entity.quest.QuestType;
import com.hits.open.world.core.quest.repository.entity.quest.TransportType;
import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import com.hits.open.world.public_interface.quest.CreateDistanceQuestDto;
import com.hits.open.world.public_interface.quest.CreatePointToPointQuestDto;
import com.hits.open.world.public_interface.quest.CreateQuestDto;
import com.hits.open.world.public_interface.route.CreateRouteDto;
import com.hits.open.world.public_interface.route.PointDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestGenerationService {
    private final GptClient gptClient;
    private final MapClient mapClient;
    private final PoiService poiService;
    private final QuestService questService;
    private final PhotoClient photoClient;
    private final RestTemplate restTemplate = new RestTemplate();

    @Scheduled(fixedRateString = "${quest.generateQuestsFixedRate}")
    public void generateQuests() {
        try {
            for (var cityName : poiService.getCities()) {
                if (Math.random() < 0.5) {
                    var poi = poiService.getRandomPoiInCity(cityName);
                    var distanceGeneratedQuest = generateRandomDistanceQuest(poi);
                    var createDistanceQuestDto = new CreateDistanceQuestDto(
                            new CreateQuestDto(
                                    distanceGeneratedQuest.name(),
                                    distanceGeneratedQuest.description(),
                                    distanceGeneratedQuest.difficultyType().name(),
                                    QuestType.DISTANCE.name(),
                                    distanceGeneratedQuest.transportType().name(),
                                    photoClient.getPhotosByCoordinates(
                                            Double.parseDouble(distanceGeneratedQuest.latitude()),
                                            Double.parseDouble(distanceGeneratedQuest.longitude())
                                    ).stream().map(this::downloadImage).toList()
                            ),
                            distanceGeneratedQuest.routeDistance(),
                            distanceGeneratedQuest.longitude(),
                            distanceGeneratedQuest.latitude()
                    );
                    questService.createDistanceQuest(createDistanceQuestDto);
                    log.info("Quest generated: %s".formatted(distanceGeneratedQuest.name()));
                } else {
                    var from = poiService.getRandomPoiInCity(cityName);
                    var to = poiService.getRandomPoiInCity(cityName);
                    if (from.equals(to)) {
                        return;
                    }
                    var pointToPointGeneratedQuest = generateRandomPointToPointQuest(from, to);
                    var createPointToPointQuestDto = new CreatePointToPointQuestDto(
                            new CreateQuestDto(
                                    pointToPointGeneratedQuest.name(),
                                    pointToPointGeneratedQuest.description(),
                                    pointToPointGeneratedQuest.difficultyType().name(),
                                    QuestType.POINT_TO_POINT.name(),
                                    pointToPointGeneratedQuest.transportType().name(),
                                    photoClient.getPhotosByCoordinates(
                                            Double.parseDouble(pointToPointGeneratedQuest.points().get(0).latitude()),
                                            Double.parseDouble(pointToPointGeneratedQuest.points().get(0).longitude())
                                    ).stream().map(this::downloadImage).toList()
                            ),
                            new CreateRouteDto(
                                    fromGeneratedPoint(pointToPointGeneratedQuest.points()),
                                    pointToPointGeneratedQuest.points().get(0).longitude(),
                                    pointToPointGeneratedQuest.points().get(0).latitude()
                            )
                    );
                    questService.createPointToPointQuest(createPointToPointQuestDto);
                    log.info("Quest generated: %s".formatted(pointToPointGeneratedQuest.name()));
                }
            }
        } catch (Exception e) {
            log.error("Error generating quests", e);
        }
    }

    public GeneratedDistanceQuest generateRandomDistanceQuest(PoiEntity poi) {
        try {
            var generatedText = gptClient.generateText("%s%n%s".formatted(QuestType.DISTANCE, poi))
                    .replace("*", "")
                    .replace("Обратите внимание, что это лишь пример названия и описания квеста, которые можно адаптировать под конкретные условия и требования.", "");
            return new GeneratedDistanceQuest(
                    getNameQuest(generatedText),
                    getDescriptionQuest(generatedText),
                    DifficultyType.getRandonDifficultyType(),
                    TransportType.getRandomTransportType(),
                    getRandomDistance(),
                    String.valueOf(poi.longitude()),
                    String.valueOf(poi.latitude())
            );
        } catch (Exception e) {
            log.error("Error generating description", e);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                log.error("Error sleeping", ex);
                throw new RuntimeException(ex);
            }
            return generateRandomDistanceQuest(poi);
        }
    }

    public GeneratedPointToPointQuest generateRandomPointToPointQuest(PoiEntity from, PoiEntity to) {
        try {
            var generatedText = gptClient.generateText("%s%nFrom%n%s%nTo%n%s".formatted(QuestType.POINT_TO_POINT, from, to))
                    .replace("*", "")
                    .replace("Обратите внимание, что это лишь пример названия и описания квеста, которые можно адаптировать под конкретные условия и требования.", "");
            var way = mapClient.getRoadBetweenTwoPoints(from.latitude(), from.longitude(), to.latitude(), to.longitude(), TransportType.getRandomTransportType());
            return new GeneratedPointToPointQuest(
                    getNameQuest(generatedText),
                    getDescriptionQuest(generatedText),
                    DifficultyType.getRandonDifficultyType(),
                    TransportType.getRandomTransportType(),
                    way
            );
        } catch (Exception e) {
            log.error("Error generating description", e);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                log.error("Error sleeping", ex);
                throw new RuntimeException(ex);
            }
            return generateRandomPointToPointQuest(from, to);
        }
    }

    private String getNameQuest(String generatedText) {
        return generatedText.split("Описание квеста:")[0]
                .split("Название квеста:")[1]
                .replace("«", "")
                .replace("»", "")
                .replace("\"", "")
                .trim();
    }

    private String getDescriptionQuest(String generatedText) {
        return generatedText.split("Описание квеста:")[1]
                .replace("«", "")
                .replace("»", "")
                .trim();
    }

    private double getRandomDistance() {
        return Math.abs(Math.random() * 1000) + 100;
    }

    private List<PointDto> fromGeneratedPoint(List<GeneratedPoint> generatedPoints) {
        List<PointDto> pointDtos = new ArrayList<>();

        for (int i = 0; i < generatedPoints.size() - 1; i++) {
            GeneratedPoint current = generatedPoints.get(i);
            GeneratedPoint next = generatedPoints.get(i + 1);

            PointDto dto = new PointDto(
                    current.latitude(),
                    current.longitude(),
                    next.latitude(),
                    next.longitude()
            );

            pointDtos.add(dto);
        }

        pointDtos.add(new PointDto(
                generatedPoints.get(generatedPoints.size() - 1).latitude(),
                generatedPoints.get(generatedPoints.size() - 1).longitude(),
                null,
                null
        ));

        return pointDtos;
    }

    public MultipartFile downloadImage(String url) {
        byte[] imageBytes = restTemplate.getForObject(url, byte[].class);
        if (imageBytes == null) {
            throw new ExceptionInApplication("Failed to download file from URL: " + url, ExceptionType.INVALID);
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
