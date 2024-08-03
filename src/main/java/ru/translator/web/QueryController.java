package ru.translator.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.translator.QueryUnit;
import ru.translator.ResponseUnit;
import ru.translator.data.ResponseUnitRepository;
import ru.translator.service.YandexTranslateService;

@Slf4j
@Controller
@RequestMapping("/query")
public class QueryController {
    private final YandexTranslateService yandexTranslateService;
    private final ResponseUnitRepository responseUnitRepository;

    @Autowired
    public QueryController(YandexTranslateService yandexTranslateService,
                           ResponseUnitRepository responseUnitRepository) {
        this.yandexTranslateService = yandexTranslateService;
        this.responseUnitRepository = responseUnitRepository;
    }

    @ModelAttribute
    public void addLanguagesToModel(Model model) {
        model.addAttribute("languages", yandexTranslateService.getSupportedLanguages());
    }

    @ModelAttribute("queryUnit")
    public QueryUnit queryUnit() {
        return new QueryUnit();
    }

    @GetMapping
    public String query() {
        return "query";
    }

    @PostMapping
    public String processTranslation(@Valid QueryUnit queryUnit, Errors errors,
                                     RedirectAttributes redirectAttributes,
                                     HttpServletRequest request) {
        if (errors.hasErrors())
            return "query";

        String ipAddress = request.getRemoteAddr();
        String originalText = queryUnit.getOriginalText();
        String translatedText = yandexTranslateService.translate(queryUnit);
        ResponseUnit responseUnit = new ResponseUnit(ipAddress, originalText, translatedText);

        responseUnitRepository.save(responseUnit);

        redirectAttributes.addFlashAttribute("responseUnit", responseUnit);
        return "redirect:/response";
    }
}
