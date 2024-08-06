package ru.translator.service.impl.yandex.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.translator.Language;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public record ListLanguagesDTO(List<Language> languages) {
    @JsonCreator
    public ListLanguagesDTO(@JsonProperty("languages") List<Language> languages) {
        this.languages = removeInvalidLanguages(languages);
        this.languages.add(new Language("auto", "Auto"));
        this.languages.sort(Comparator.comparing(Language::code));
    }

    private List<Language> removeInvalidLanguages(List<Language> languages) {
        return languages.stream()
                .filter(language -> language.code() != null && language.name() != null)
                .collect(Collectors.toList());
    }
}
