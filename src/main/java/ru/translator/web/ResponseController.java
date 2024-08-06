package ru.translator.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.translator.Response;

@Controller
@RequestMapping("/response")
public class ResponseController {
    @GetMapping
    public String response(Model model,
                           @ModelAttribute("response") Response response) {
        model.addAttribute("response", response);
        return "response";
    }

    @PostMapping
    public String finish() {
        return "redirect:/";
    }
}
