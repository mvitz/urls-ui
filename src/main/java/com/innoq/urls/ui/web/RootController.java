package com.innoq.urls.ui.web;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.innoq.urls.ui.domain.HashService;
import com.innoq.urls.ui.domain.URL;

@Controller
@RequestMapping(value = "/",
        produces = MediaType.TEXT_HTML_VALUE)
public final class RootController {

    private final HashService hashService;

    @Autowired
    public RootController(HashService hashService) {
        this.hashService = hashService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String showForm(Model model) {
        model.addAttribute("url", new URL());
        return "form";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String createHash(
            @Valid URL url,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "form";
        }

        final String hash = hashService.hash(url.getValue());
        redirectAttributes.addFlashAttribute("message", "http://localhost:8080/" + hash);

        return "redirect:/";
    }

    @RequestMapping(value = "/{hash}", method = RequestMethod.GET)
    public String resolveHash(@PathVariable String hash, RedirectAttributes redirectAttributes) {
        final Optional<String> value = hashService.resolve(hash);
        if (value.isPresent()) {
            return "redirect:" + value.get();
        } else {
            redirectAttributes.addFlashAttribute("message", "No URL found for hash: " + hash);
            return "redirect:/";
        }
    }

}
