package ru.translator.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.translator.Language;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public record LanguagesDTO(List<Language> languages) {
    @JsonCreator
    public LanguagesDTO(@JsonProperty("languages") List<Language> languages) {
        this.languages = removeLanguagesWithAtLeastOneNullFieldAndSortedRemainingByCode(languages);
    }

    private List<Language> removeLanguagesWithAtLeastOneNullFieldAndSortedRemainingByCode(List<Language> languages) {
        return languages.stream()
                .filter(language -> language.code() != null && language.name() != null)
                .sorted(Comparator.comparing(Language::code))
                .collect(Collectors.toList());
    }
}
