package org.bseber.haushalt.payees.mapping;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/payees/mapping")
class PayeeNameMappingController {

    private final PayeeNameMappingService payeeNameMappingService;

    PayeeNameMappingController(PayeeNameMappingService payeeNameMappingService) {
        this.payeeNameMappingService = payeeNameMappingService;
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        // TODO use nice UX for lists greater than X
        // default of 256 can be reached easily with importing transactions
        webDataBinder.setAutoGrowCollectionLimit(1000);
    }

    @GetMapping
    public String payees(Model model) {

        final List<PayeeNameMappingDto.Mapping> mappings = payeeNameMappingService.findAllPayeeMappings().stream()
            .map(PayeeNameMappingController::toPayeeMappingDto)
            .toList();

        model.addAttribute("nameMapping", new PayeeNameMappingDto(mappings));

        return "payees/mapping/index";
    }

    @PostMapping("/name")
    public String editNameMappings(@ModelAttribute("nameMapping") PayeeNameMappingDto nameMappingDto, Model model) {

        final List<PayeeNameMapping> mappings = nameMappingDto.getMappings().stream()
            .map(PayeeNameMappingController::toPayeeNameMapping)
            .toList();

        payeeNameMappingService.updateNameMappings(mappings);

        return "redirect:/payees/mapping";
    }

    private static PayeeNameMappingDto.Mapping toPayeeMappingDto(PayeeNameMapping payeeNameMapping) {
        return new PayeeNameMappingDto.Mapping(payeeNameMapping.originalName(), payeeNameMapping.mappedName());
    }

    private static PayeeNameMapping toPayeeNameMapping(PayeeNameMappingDto.Mapping dto) {
        return new PayeeNameMapping(dto.getOriginalName(), dto.getMappedName());
    }
}
