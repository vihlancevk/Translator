package ru.translator.service;

import org.springframework.stereotype.Service;
import ru.translator.Language;
import ru.translator.QueryUnit;

import java.util.List;

@Service
public interface YandexTranslateService {
    List<Language> getSupportedLanguages();
    String translate(QueryUnit translationUnit);
}
