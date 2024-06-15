package com.hits.open.world.client.gpt;

public record GptResponse(
        ResultResponseDto result
) {
    public record ResultResponseDto(
            AlternativesResponseDto[] alternatives,
            UsageResponseDto usage,
            String modelVersion
    ) {
        public record AlternativesResponseDto(
                MessageResponseDto message,
                String status
        ) {
            public record MessageResponseDto(
                    String text,
                    String role
            ) {

            }
        }

        public record UsageResponseDto(
                String inputTextTokens,
                String completionTokens,
                String totalTokens
        ) {

        }
    }
}
