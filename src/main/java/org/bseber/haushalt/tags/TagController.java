package org.bseber.haushalt.tags;

import jakarta.servlet.http.HttpServletRequest;
import org.bseber.haushalt.core.Budget;
import org.bseber.haushalt.core.Money;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/tags")
class TagController {

    private final TagService tagService;
    private final UpdateTagBucketValidator validator;

    TagController(TagService tagService, UpdateTagBucketValidator validator) {
        this.tagService = tagService;
        this.validator = validator;
    }

    @GetMapping
    public String tags(Model model, HttpServletRequest request) {

        final Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);

        if (inputFlashMap == null || !inputFlashMap.containsKey("tags")) {
            final List<TagDto> tags = tagService.findAllTags().stream().map(TagController::toTagDto).toList();
            model.addAttribute("tags", new TagBucketDto(tags));
        }

        return "tags/index";
    }

    @PostMapping
    public String update(@ModelAttribute("tags") TagBucketDto tagBucket, Errors errors,
                         RedirectAttributes redirectAttributes) {

        validator.validate(tagBucket, errors);
        if (errors.hasErrors()) {
            redirectAttributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "tags", errors);
            redirectAttributes.addFlashAttribute("tags", tagBucket);
        } else {
            final List<Tag> tags = tagBucket.getTags().stream().map(TagController::toTag).toList();
            tagService.updateTags(tags);
            redirectAttributes.addFlashAttribute("tagsUpdated", true);
        }

        return "redirect:/tags";
    }

    private static TagDto toTagDto(Tag tag) {
        final TagDto dto = new TagDto();
        dto.setId(tag.id().value());
        dto.setName(tag.name().value());
        dto.setDescription(tag.description().value());
        dto.setBudget(tag.budget().map(Budget::amount).map(Money::amount).orElse(null));
        dto.setBudgetType(tag.budget().map(Budget::type).orElse(null));
        return dto;
    }

    private static Tag toTag(TagDto dto) {
        final Tag.Id id = new Tag.Id(dto.getId());
        final TagName name = TagName.sanitized(dto.getName());
        final TagDescription description = TagDescription.sanitized(dto.getDescription());
        return new Tag(id, name, description, dto.getColor(), budget(dto));
    }

    private static Budget budget(TagDto dto) {
        return dto.getBudget() == null ? null : new Budget(Money.ofEUR(dto.getBudget()), dto.getBudgetType());
    }
}
