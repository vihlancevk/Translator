package ru.translator.service.impl.yandex.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record DetectLanguageDTO(String languageCode) {
	@JsonCreator
	public DetectLanguageDTO(@JsonProperty("languageCode") String languageCode) {
		this.languageCode = languageCode;
	}
}
