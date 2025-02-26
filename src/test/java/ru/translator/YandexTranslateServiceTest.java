package ru.translator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.translator.service.impl.yandex.YandexTranslateService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class YandexTranslateServiceTest {
	@Autowired
	private YandexTranslateService yandexTranslateService;

	@Test
	public void testDetectLanguageBadQuery() {
		Query badQuery = new Query();
		badQuery.setOriginalText("Было у отца три сына");
		badQuery.setSourceLanguageCode("en");
		badQuery.setTargetLanguageCode("ru");
		String detectLanguage = yandexTranslateService.detectLanguage(badQuery);
		assertNotEquals(detectLanguage, "en");
		assertEquals(detectLanguage, "ru");
	}

	@Test
	public void testDetectLanguageGoodQuery() {
		Query goodQuery = new Query();
		goodQuery.setOriginalText("Было у отца три сына");
		goodQuery.setSourceLanguageCode("ru");
		goodQuery.setTargetLanguageCode("en");
		String detectLanguage = yandexTranslateService.detectLanguage(goodQuery);
		assertNotEquals(detectLanguage, "en");
		assertEquals(detectLanguage, "ru");
	}

	@Test
	public void testListLanguages() {
		List<Language> languages = yandexTranslateService.listLanguages();
		assertFalse(languages.isEmpty());
	}

	@Test
	public void testTranslateQueryAutoRu() {
		Query query = new Query();
		query.setOriginalText("Green apple");
		query.setSourceLanguageCode("auto");
		query.setTargetLanguageCode("ru");
		String translatedText = yandexTranslateService.translate(query);
		assertEquals(translatedText, "Зеленый яблоко");
	}

	@Test
	public void testTranslateQueryRuEn() {
		Query query = new Query();
		query.setOriginalText("Кот");
		query.setSourceLanguageCode("ru");
		query.setTargetLanguageCode("en");
		String translatedText = yandexTranslateService.translate(query);
		assertEquals(translatedText, "Cat");
	}

	@Test
	public void testTranslateQueryRuId() {
		Query query = new Query();
		query.setOriginalText("Трава Расти Дерево Диплом Учёба");
		query.setSourceLanguageCode("ru");
		query.setTargetLanguageCode("id");
		String translatedText = yandexTranslateService.translate(query);
		assertEquals(translatedText, "Rumput Tumbuh Pohon Diploma Studi");
	}
}
