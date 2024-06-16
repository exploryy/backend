package com.hits.open.world.core.quest;

import com.hits.open.world.client.gpt.GptClient;
import com.hits.open.world.client.map.MapClient;
import com.hits.open.world.core.poi.PoiEntity;
import com.hits.open.world.core.quest.repository.entity.generated.GeneratedDistanceQuest;
import com.hits.open.world.core.quest.repository.entity.generated.GeneratedPointToPointQuest;
import com.hits.open.world.core.quest.repository.entity.quest.DifficultyType;
import com.hits.open.world.core.quest.repository.entity.quest.QuestType;
import com.hits.open.world.core.quest.repository.entity.quest.TransportType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestGenerationService {
    private final GptClient gptClient;
    private final MapClient mapClient;

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
                .trim();
    }

    private String getDescriptionQuest(String generatedText) {
        return generatedText.split("Описание квеста:")[1].trim();
    }

    private double getRandomDistance() {
        return Math.abs(Math.random() * 1000) + 100;
    }
}
