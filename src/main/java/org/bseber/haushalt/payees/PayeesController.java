package org.bseber.haushalt.payees;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/payees")
class PayeesController {

    @GetMapping
    public String payees() {
        return "forward:/payees/mapping";
    }
}
