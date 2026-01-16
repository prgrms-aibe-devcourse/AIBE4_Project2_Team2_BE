package kr.java.aibe4_project2_team2_be.majormate.domain.recommend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

public class GeminiMessageDto {

    // Gemini로 보낼 데이터
    @Getter
    @Builder
    public static class Request {
        private List<Content> contents;
        private GenerationConfig generationConfig;

        @Getter
        @Builder
        public static class Content {
            private String role;
            private List<Part> parts;
        }

        @Getter
        @Builder
        public static class Part {
            private String text;
        }

        @Getter
        @Builder
        public static class GenerationConfig {
            private String responseMimeType; // "application/json" 강제 설정용
        }
    }

    // Gemini에서 들어온 데이터
    @Getter
    @NoArgsConstructor
    public static class Response {
        private List<Candidate> candidates;

        @Getter
        @NoArgsConstructor
        public static class Candidate {
            private Content content;
        }

        @Getter
        @NoArgsConstructor
        public static class Content {
            private List<Part> parts;
        }

        @Getter
        @NoArgsConstructor
        public static class Part {
            private String text;
        }
    }
}