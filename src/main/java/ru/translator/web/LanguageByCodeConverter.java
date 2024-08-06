package ru.translator.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.translator.Language;
import ru.translator.service.TranslateService;

@Component
public class LanguageByCodeConverter implements Converter<String, Language> {
    private final TranslateService translateService;

    @Autowired
    public LanguageByCodeConverter(TranslateService translateService) {
        this.translateService = translateService;
    }

    @Override
    public Language convert(String code) {
        for (Language language : translateService.listLanguages())
            if (language.code().equals(code))
                return language;
        return null;
    }
}
