package ru.translator;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class QueryUnit {
    @NotNull
    @Size(min=1, max=100, message="The length of the source text must be no less than 1 and no more than 100.")
    private String originalText;

    @NotNull private Language sourceLanguage;
    @NotNull private Language targetLanguage;

    public String[] getOriginalTextAsWordsWithLengthAtLeastOneSymbol() {
        return originalText.trim().split(" ");
    }
}
