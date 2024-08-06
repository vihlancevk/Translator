package ru.translator.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.translator.Query;
import ru.translator.Response;
import ru.translator.data.ResponseURepository;
import ru.translator.service.TranslateService;

@Controller
@RequestMapping("/query")
public class QueryController {
    private final TranslateService translateService;
    private final ResponseURepository responseURepository;

    @Autowired
    public QueryController(TranslateService translateService,
                           ResponseURepository responseURepository) {
        this.translateService = translateService;
        this.responseURepository = responseURepository;
    }

    @ModelAttribute
    public void addLanguagesToModel(Model model) {
        model.addAttribute("languages", translateService.listLanguages());
    }

    @ModelAttribute("query")
    public Query queryUnit() {
        return new Query();
    }

    @GetMapping
    public String query() {
        return "query";
    }

    @PostMapping
    public String processTranslation(@Valid Query query, Errors errors,
                                     RedirectAttributes redirectAttributes,
                                     HttpServletRequest request) {
        if (errors.hasErrors())
            return "query";

        String ipAddress = request.getRemoteAddr();
        String originalText = query.getOriginalText();
        String translatedText = translateService.translate(query);
        Response response = new Response(ipAddress, originalText, translatedText);

        responseURepository.save(response);

        redirectAttributes.addFlashAttribute("response", response);
        return "redirect:/response";
    }
}
