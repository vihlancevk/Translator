package ru.translator.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.translator.ResponseUnit;

@Slf4j
@Controller
@RequestMapping("/response")
public class ResponseController {
    @GetMapping
    public String response(Model model,
                           @ModelAttribute("responseUnit") ResponseUnit responseUnit) {
        model.addAttribute("responseUnit", responseUnit);
        return "response";
    }

    @PostMapping
    public String finish() {
        return "redirect:/";
    }
}
