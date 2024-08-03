package ru.translator;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class ResponseUnit {
	@Id
	private long id;
	private String ipAddress;
	private String originalText;
	private String translatedText;

	public ResponseUnit(String ipAddress, String originalText, String translatedText) {
		this.ipAddress = ipAddress;
		this.originalText = originalText;
		this.translatedText = translatedText;
	}
}
