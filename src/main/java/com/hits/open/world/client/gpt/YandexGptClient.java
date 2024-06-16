package com.hits.open.world.client.gpt;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
public class YandexGptClient implements GptClient {
    private static final String GPT_PROMPT = """
            необходимо написать одну вариацию название и описание квеста.
            на вход я буду давать тебе такую структуру объекта
             {
              "name": string,
              "review": double,
              "count_review": int,
              "latitude": double,
              "longitude": double
             }
            Также буду уточнять какой тип квеста POINT_TO_POINT, DISTANCE.
            Для POINT_TO_POINT я буду передавать две сущности, а для DISTANCE одну.
            Суть POINT_TO_POINT добраться от одной точки до другой, а суть DISTANCE удалиться от точки на определённое расстояние от этой точки.
                        
            Необходимое креативное описание и название подобно этому: название "Пойдем бухнем", описание "Давайте сходим и прибухнем" В описании нужно придумать какую-то историю связанную с названиями мест.
            не добавляй эту надпись: "Обратите внимание, что это лишь пример названия и описания квеста, которые можно адаптировать под конкретные условия и требования.", а также правила
            """;

    private final WebClient webClient;
    private final String modelUri;

    @Override
    public String generateText(String text) {
        var response = webClient.post()
                .bodyValue(createRequest(text))
                .retrieve()
                .bodyToMono(GptResponse.class)
                .block();
        return response.result().alternatives()[0].message().text();
    }

    private GptRequest createRequest(String text) {
        return new GptRequest(
                modelUri,
                new GptRequest.MessageRequestDto[]{
                        new GptRequest.MessageRequestDto(GPT_PROMPT, "system"),
                        new GptRequest.MessageRequestDto(text, "user")
                },
                new GptRequest.CompletionOptionsRequestDto(
                        false,
                        "500",
                        "0.7"
                )
        );
    }
}
