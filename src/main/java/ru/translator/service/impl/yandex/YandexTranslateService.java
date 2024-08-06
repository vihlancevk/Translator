package ru.translator.service.impl.yandex;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.translator.Language;
import ru.translator.Query;
import ru.translator.service.impl.yandex.dto.DetectLanguageDTO;
import ru.translator.service.impl.yandex.dto.ListLanguagesDTO;
import ru.translator.service.impl.yandex.dto.TranslateDTO;
import ru.translator.service.TranslateService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class YandexTranslateService implements TranslateService {
    private final static String BASE_URL = "https://translate.api.cloud.yandex.net/translate/v2";
    private final RestTemplate rest = new RestTemplate();
    @Value("${yandex.translate.api.key}")
    private String apiKey;

    @Override
    public String detectLanguage(Query query) {
        String url = createUrlForDetectLanguage(query);
        HttpEntity<HttpHeaders> entity = createHttpHeadersEntity();
        ResponseEntity<DetectLanguageDTO> response = rest.exchange(url, HttpMethod.POST, entity, DetectLanguageDTO.class);
        if (response.hasBody()) {
            return response.getBody().languageCode();
        } else {
            String statusCode = response.getStatusCode().toString();
            log.warn(createWarnMessage("detectLanguage", statusCode));
            return statusCode;
        }
    }

    private String createUrlForDetectLanguage(Query query) {
        StringBuilder sb = new StringBuilder(BASE_URL + "/detect");
        sb.append("?");
        sb.append("text").append("=").append(query.getOriginalText());
        return sb.toString();
    }

    @Override
    public List<Language> listLanguages() {
        String url = createUrlForLanguages();
        HttpEntity<HttpHeaders> entity = createHttpHeadersEntity();
        ResponseEntity<ListLanguagesDTO> response = rest.exchange(url, HttpMethod.POST, entity, ListLanguagesDTO.class);
        if (response.hasBody()) {
            return response.getBody().languages();
        } else {
            String statusCode = response.getStatusCode().toString();
            log.warn(createWarnMessage("listLanguages", statusCode));
            return List.of();
        }
    }

    private String createUrlForLanguages() {
        return BASE_URL + "/languages";
    }

    @Override
    public String translate(Query query) {
        handleAutoLanguageCode(query);

        String[] originalTextAsWords = query.toWords();
        String[] translatedTextAsWords = new String[originalTextAsWords.length];

        int numberOfThreads = 10;
        try (ExecutorService service = Executors.newFixedThreadPool(numberOfThreads)) {
            for (int i = 0; i < originalTextAsWords.length; i++) {
                int finalIndex = i;
                service.execute(() -> {
                        String url = createUrlForTranslate(query, originalTextAsWords[finalIndex]);
                        HttpEntity<HttpHeaders> entity = createHttpHeadersEntity();
                        ResponseEntity<TranslateDTO> response = rest.exchange(url, HttpMethod.POST, entity, TranslateDTO.class);
                        if (response.hasBody()) {
                            translatedTextAsWords[finalIndex] = response.getBody().getTranslatedText();
                        } else {
                            String statusCode = response.getStatusCode().toString();
                            log.warn(createWarnMessage("translate", statusCode));
                            translatedTextAsWords[finalIndex] = statusCode;
                        }
                    }
                );
            }

            service.shutdown();
            service.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            return "";
        }

        return String.join(" ", translatedTextAsWords);
    }

    private void handleAutoLanguageCode(Query query) {
        if (query.getSourceLanguageCode().equals("auto")) {
            query.setSourceLanguageCode(detectLanguage(query));
        }
    }

    private String createUrlForTranslate(Query query, String word) {
        StringBuilder sb = new StringBuilder(BASE_URL + "/translate");
        sb.append("?");
        sb.append("sourceLanguageCode").append("=").append(query.getSourceLanguageCode());
        sb.append("&");
        sb.append("targetLanguageCode").append("=").append(query.getTargetLanguageCode());
        sb.append("&");
        sb.append("texts").append("=").append(word);
        return sb.toString();
    }

    private HttpEntity<HttpHeaders> createHttpHeadersEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Api-Key " + apiKey);
        return new HttpEntity<>(headers);
    }

    private String createWarnMessage(String originalMethodeName, String statusCode) {
        return originalMethodeName + ": " + statusCode;
    }
}
