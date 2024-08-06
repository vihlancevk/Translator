package ru.translator.service.impl.yandex.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;

public record TranslateDTO(List<Translation> translations) {
    @JsonCreator
    public TranslateDTO(@JsonProperty("translations") List<Translation> translations) {
        this.translations = translations;
    }

    public String getTranslatedText() {
        return translations.stream()
                .map(Translation::text)
                .collect(Collectors.joining(" "));
    }

    public record Translation(String text) {
        @JsonCreator
        public Translation(@JsonProperty("text") String text) {
            this.text = text;
        }
    }
}
