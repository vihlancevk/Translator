package ru.translator;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Query {
    @NotNull
    @Size(min=1, max=128, message="The length of the source text must be no less than 1 and no more than 128.")
    private String originalText;

    @NotNull private String sourceLanguageCode;
    @NotNull private String targetLanguageCode;

    public String[] toWords() {
        return originalText.trim().split(" ");
    }
}
