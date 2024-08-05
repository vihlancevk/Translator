package ru.translator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.translator.service.impl.YandexTranslateServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class YandexTranslateServiceImplTest {
	@Autowired
	private YandexTranslateServiceImpl yandexTranslateService;

	@Test
	public void testDetectLanguageBadQuery() {
		QueryUnit badQueryUnit = new QueryUnit();
		badQueryUnit.setOriginalText("Было у отца три сына");
		badQueryUnit.setSourceLanguage(new Language("en", "English"));
		badQueryUnit.setTargetLanguage(new Language("ru", "русский"));
		String detectLanguage = yandexTranslateService.detectLanguage(badQueryUnit);
		assertNotEquals(detectLanguage, "en");
		assertEquals(detectLanguage, "ru");
	}

	@Test
	public void testDetectLanguageGoodQuery() {
		QueryUnit goodQueryUnit = new QueryUnit();
		goodQueryUnit.setOriginalText("Было у отца три сына");
		goodQueryUnit.setSourceLanguage(new Language("ru", "русский"));
		goodQueryUnit.setTargetLanguage(new Language("en", "English"));
		String detectLanguage = yandexTranslateService.detectLanguage(goodQueryUnit);
		assertNotEquals(detectLanguage, "en");
		assertEquals(detectLanguage, "ru");
	}

	@Test
	public void testListLanguages() {
		List<Language> languages = yandexTranslateService.listLanguages();
		assertFalse(languages.isEmpty());
	}

	@Test
	public void testTranslateBadQuery() {
		QueryUnit badQueryUnit = new QueryUnit();
		badQueryUnit.setOriginalText("Было у отца три сына");
		badQueryUnit.setSourceLanguage(new Language("en", "English"));
		badQueryUnit.setTargetLanguage(new Language("ru", "русский"));
		String translatedText = yandexTranslateService.translate(badQueryUnit);
		assertEquals(translatedText, "Language of original text is different from chosen beginning language for translate.");
	}

	@Test
	public void testTranslateGoodQuery() {
		QueryUnit goodQueryUnit = new QueryUnit();
		goodQueryUnit.setOriginalText("Было у отца три сына");
		goodQueryUnit.setSourceLanguage(new Language("ru", "русский"));
		goodQueryUnit.setTargetLanguage(new Language("en", "English"));
		String translatedText = yandexTranslateService.translate(goodQueryUnit);
		assertNotEquals(translatedText, "Language of original text is different from chosen beginning language for translate.");
	}
}
