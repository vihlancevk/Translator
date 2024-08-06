package ru.translator.service.impl.yandex;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.translator.Language;
import ru.translator.QueryUnit;
import ru.translator.service.impl.yandex.dto.DetectLanguageDTO;
import ru.translator.service.impl.yandex.dto.ListLanguagesDTO;
import ru.translator.service.impl.yandex.dto.TranslateDTO;
import ru.translator.service.TranslateService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static ru.translator.constant.Constant.*;

@Slf4j
@Component
public class YandexTranslateService implements TranslateService {
    private final static String BASE_URL = "https://translate.api.cloud.yandex.net/translate/v2";
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${yandex.translate.api.key}")
    private String apiKey;

    @Override
    public String detectLanguage(QueryUnit queryUnit) {
        String url = createUrlForDetectLanguage(queryUnit);
        HttpEntity<HttpHeaders> entity = createHttpHeadersEntity();
        HttpEntity<DetectLanguageDTO> response = restTemplate.exchange(url, HttpMethod.POST, entity, DetectLanguageDTO.class);
        if (response.getBody() != null) {
            return response.getBody().languageCode();
        } else {
            log.warn(createWarnMessage("detectLanguage"));
            return "";
        }
    }

    private String createUrlForDetectLanguage(QueryUnit queryUnit) {
        StringBuilder sb = new StringBuilder(BASE_URL + "/detect");
        sb.append(QUESTION_SIGN);
        sb.append("text").append(EQUAL_SIGN).append(queryUnit.getOriginalText());
        sb.append(AMPERSAND_SIGN);
        sb.append("languageCodeHints").append(EQUAL_SIGN).append(queryUnit.getSourceLanguage().code());
        return sb.toString();
    }

    @Override
    public List<Language> listLanguages() {
        String url = createUrlForLanguages();
        HttpEntity<HttpHeaders> entity = createHttpHeadersEntity();
        HttpEntity<ListLanguagesDTO> response = restTemplate.exchange(url, HttpMethod.POST, entity, ListLanguagesDTO.class);
        if (response.getBody() != null) {
            return response.getBody().languages();
        } else {
            log.warn(createWarnMessage("listLanguages"));
            return List.of();
        }
    }

    private String createUrlForLanguages() {
        return BASE_URL + "/languages";
    }

    @Override
    public String translate(QueryUnit queryUnit) {
        if (!originalTextHasCorrectSourceLanguageInQueryUnit(queryUnit))
            return "Language of original text is different from chosen beginning language for translate.";

        String[] originalTextAsWords = queryUnit.getOriginalTextAsWordsWithLengthAtLeastOneSymbol();
        String[] translatedTextAsWords = new String[originalTextAsWords.length];

        int numberOfThreads = 10;
        try (ExecutorService service = Executors.newFixedThreadPool(numberOfThreads)) {
            for (int i = 0; i < originalTextAsWords.length; i++) {
                int finalIndex = i;
                service.execute(() -> {
                        String url = createUrlForTranslate(queryUnit, originalTextAsWords[finalIndex]);
                        HttpEntity<HttpHeaders> entity = createHttpHeadersEntity();
                        HttpEntity<TranslateDTO> response = restTemplate.exchange(url, HttpMethod.POST, entity, TranslateDTO.class);
                        if (response.getBody() != null) {
                            translatedTextAsWords[finalIndex] = response.getBody().getTranslatedText();
                        } else {
                            log.warn(createWarnMessage("translate"));
                            translatedTextAsWords[finalIndex] = "<error>";
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

    private boolean originalTextHasCorrectSourceLanguageInQueryUnit(QueryUnit queryUnit) {
        String languageCode = detectLanguage(queryUnit);
        return queryUnit.getSourceLanguage().code().equals(languageCode);
    }

    private String createUrlForTranslate(QueryUnit queryUnit, String word) {
        StringBuilder sb = new StringBuilder(BASE_URL + "/translate");
        sb.append(QUESTION_SIGN);
        sb.append("sourceLanguageCode").append(EQUAL_SIGN).append(queryUnit.getSourceLanguage().code());
        sb.append(AMPERSAND_SIGN);
        sb.append("targetLanguageCode").append(EQUAL_SIGN).append(queryUnit.getTargetLanguage().code());
        sb.append(AMPERSAND_SIGN);
        sb.append("texts").append(EQUAL_SIGN).append(word);
        return sb.toString();
    }

    private HttpEntity<HttpHeaders> createHttpHeadersEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Api-Key " + apiKey);
        return new HttpEntity<>(headers);
    }

    private String createWarnMessage(String originalMethodeName) {
        return "There is no body in response from '" + originalMethodeName + "' method.";
    }
}
