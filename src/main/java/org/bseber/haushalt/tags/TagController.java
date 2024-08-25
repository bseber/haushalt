package org.bseber.haushalt.tags;

import org.bseber.haushalt.core.Budget;
import org.bseber.haushalt.core.Money;
import org.bseber.haushalt.tags.TagDto.Update;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static org.springframework.validation.BindingResult.MODEL_KEY_PREFIX;

@Controller
@RequestMapping("/tags")
class TagController {

    private final TagService tagService;

    TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public String tags(Model model) {
        prepareTags(model);
        return "tags/index";
    }

    @PostMapping
    public String update(@Validated(Update.class) @ModelAttribute("tags") TagBucketDto tagBucket, Errors errors,
                         RedirectAttributes redirectAttributes) {

        if (errors.hasErrors()) {
            redirectAttributes.addFlashAttribute(MODEL_KEY_PREFIX + "tags", errors);
        } else {
            final List<Tag> tags = tagBucket.getTags().stream().map(TagController::toTag).toList();
            tagService.updateTags(tags);
            redirectAttributes.addFlashAttribute("tagsUpdated", true);
        }

        return "redirect:/tags";
    }

    private void prepareTags(Model model) {
        final List<TagDto> tags = tagService.findAllTags().stream().map(TagController::toTagDto).toList();
        model.addAttribute("tags", new TagBucketDto(tags));
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
