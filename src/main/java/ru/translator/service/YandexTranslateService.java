package ru.translator.service;

import org.springframework.stereotype.Service;
import ru.translator.Language;
import ru.translator.QueryUnit;

import java.util.List;

@Service
public interface YandexTranslateService {
    String detectLanguage(QueryUnit queryUnit);

    List<Language> listLanguages();

    String translate(QueryUnit queryUnit);
}
