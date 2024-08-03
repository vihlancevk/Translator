package ru.translator.web;

import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.translator.Language;
import ru.translator.service.YandexTranslateService;

@Component
public class LanguageByCodeConverter implements Converter<String, Language> {
    private final YandexTranslateService yandexTranslateService;

    @Autowired
    public LanguageByCodeConverter(YandexTranslateService yandexTranslateService) {
        this.yandexTranslateService = yandexTranslateService;
    }

    @Override
    public Language convert(@Nullable String code) {
        for (Language language : yandexTranslateService.getSupportedLanguages())
            if (language.code().equals(code))
                return language;
        return null;
    }
}
