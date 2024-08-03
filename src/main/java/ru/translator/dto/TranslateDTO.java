package ru.translator.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TranslateDTO(List<Translation> translations) {
    @JsonCreator
    public TranslateDTO(@JsonProperty("translations") List<Translation> translations) {
        this.translations = translations;
    }

    public String getTranslatedText() {
        String[] words = new String[translations.size()];
        for (int i = 0; i < translations.size(); i++)
            words[i] = translations.get(i).text();
        return String.join(" ", words);
    }

    public record Translation(String text) {
        @JsonCreator
        public Translation(@JsonProperty("text") String text) {
            this.text = text;
        }
    }
}
