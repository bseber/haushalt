package org.bseber.haushalt.settings;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/settings")
class SettingsController {

    @GetMapping
    public String settings() {
        return "settings/index";
    }
}
