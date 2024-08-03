package ru.translator.service.impl;

import org.springframework.beans.factory.annotation.Value;
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
    private final static String BASE_URL = "https://translate.api.cloud.yandex.net/translate/v2";
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${yandex.translate.api.key}")
    private String apiKey;

    @Override
    public List<Language> getSupportedLanguages() {
        String url = createUrlForLanguages();
        HttpEntity<HttpHeaders> entity = createHttpHeadersEntity();
        HttpEntity<LanguagesDTO> response = restTemplate.exchange(url, HttpMethod.POST, entity, LanguagesDTO.class);
        return (response.getBody() == null) ? List.of() : response.getBody().languages();
    }

    private String createUrlForLanguages() {
        return BASE_URL + "/languages";
    }

    @Override
    public String translate(QueryUnit translationUnit) {
        String[] originalTextAsWords = translationUnit.getOriginalTextAsWordsWithLengthAtLeastOneSymbol();
        String[] translatedTextAsWords = new String[originalTextAsWords.length];

        int numberOfThreads = 10;
        try (ExecutorService service = Executors.newFixedThreadPool(numberOfThreads)) {
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
        return UriComponentsBuilder.fromHttpUrl(BASE_URL + "/translate")
                .queryParam("sourceLanguageCode", translationUnit.getSourceLanguage().code())
                .queryParam("targetLanguageCode", translationUnit.getTargetLanguage().code())
                .queryParam("texts", List.of(word))
                .toUriString();
    }

    private HttpEntity<HttpHeaders> createHttpHeadersEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Api-Key " + apiKey);
        return new HttpEntity<>(headers);
    }
}
