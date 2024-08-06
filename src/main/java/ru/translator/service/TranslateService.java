package ru.translator.service;

import org.springframework.stereotype.Service;
import ru.translator.Language;
import ru.translator.Query;

import java.util.List;

@Service
public interface TranslateService {
    String detectLanguage(Query query);

    List<Language> listLanguages();

    String translate(Query query);
}
