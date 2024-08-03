package ru.translator.service.impl;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.translator.Language;
import ru.translator.QueryUnit;
import ru.translator.dto.LanguagesDTO;
import ru.translator.dto.TranslateDTO;
import ru.translator.service.YandexTranslateService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class YandexTranslateServiceImpl implements YandexTranslateService {
    private final static int N_THREADS = 10;
    private final static String CONTENT_TYPE = "application/json";
    private final static String AUTHORIZATION = "Api-Key ...";
    private final static String BASE_URL = "https://translate.api.cloud.yandex.net/translate/v2";
    private final static String LANGUAGES = "/languages";
    private final static String TRANSLATE = "/translate";

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<Language> getSupportedLanguages() {
        String url = createUrlForLanguages();
        HttpEntity<HttpHeaders> entity = createHttpHeadersEntity();
        HttpEntity<LanguagesDTO> response = restTemplate.exchange(url, HttpMethod.POST, entity, LanguagesDTO.class);
        return (response.getBody() == null) ? List.of() : response.getBody().languages();
    }

    private String createUrlForLanguages() {
        return BASE_URL + LANGUAGES;
    }

    @Override
    public String translate(QueryUnit translationUnit) {
        String[] originalTextAsWords = translationUnit.getOriginalTextAsWordsWithLengthAtLeastOneSymbol();
        String[] translatedTextAsWords = new String[originalTextAsWords.length];

        try (ExecutorService service = Executors.newFixedThreadPool(N_THREADS)) {
            for (int i = 0; i < originalTextAsWords.length; i++) {
                int finalIndex = i;
                service.execute(() -> {
                        String url = createUrlForTranslate(translationUnit, originalTextAsWords[finalIndex]);
                        HttpEntity<HttpHeaders> entity = createHttpHeadersEntity();
                        HttpEntity<TranslateDTO> response = restTemplate.exchange(url, HttpMethod.POST, entity, TranslateDTO.class);
                        translatedTextAsWords[finalIndex] = (response.getBody() == null)
                                ? "null"
                                : response.getBody().getTranslatedText();
                    }
                );
            }

            service.shutdown();
            service.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            return "";
        }

        return String.join(" ", translatedTextAsWords);
    }

    private String createUrlForTranslate(QueryUnit translationUnit, String word) {
        return UriComponentsBuilder.fromHttpUrl(BASE_URL + TRANSLATE)
                .queryParam("sourceLanguageCode", translationUnit.getSourceLanguage().code())
                .queryParam("targetLanguageCode", translationUnit.getTargetLanguage().code())
                .queryParam("texts", List.of(word))
                .toUriString();
    }

    private HttpEntity<HttpHeaders> createHttpHeadersEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", CONTENT_TYPE);
        headers.set("Authorization", AUTHORIZATION);
        return new HttpEntity<>(headers);
    }
}
