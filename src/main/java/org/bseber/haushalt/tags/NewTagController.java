package org.bseber.haushalt.tags;

import org.bseber.haushalt.core.Budget;
import org.bseber.haushalt.core.Money;
import org.bseber.haushalt.tags.TagDto.Create;
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
import java.util.stream.IntStream;

import static org.springframework.validation.BindingResult.MODEL_KEY_PREFIX;

@Controller
@RequestMapping("/tags/new")
class NewTagController {

    private final TagService tagService;

    NewTagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public String tags(Model model) {

        final List<TagDto> newTagDtos = IntStream.range(0, 5).mapToObj(i -> new TagDto()).toList();
        model.addAttribute("newTags", new TagBucketDto(newTagDtos));

        return "tags/new";
    }

    @PostMapping
    public String newTags(@Validated(Create.class) @ModelAttribute("newTags") TagBucketDto tagBucket, Errors errors,
                          RedirectAttributes redirectAttributes) {

        if (errors.hasErrors()) {
            redirectAttributes.addFlashAttribute(MODEL_KEY_PREFIX + "newTags", errors);
            return "redirect:/tags/new";
        } else {
            final List<NewTag> newTags = tagBucket.getTags().stream().map(NewTagController::toNewTag).toList();
            tagService.createNewTag(newTags);
            redirectAttributes.addFlashAttribute("newTagsCreated", true);
            return "redirect:/tags";
        }
    }

    private static NewTag toNewTag(TagDto dto) {
        final TagName name = TagName.sanitized(dto.getName());
        final TagDescription description = TagDescription.sanitized(dto.getDescription());
        return new NewTag(name, description, dto.getColor(), budget(dto));
    }

    private static Budget budget(TagDto dto) {
        return dto.getBudget() == null ? null : new Budget(Money.ofEUR(dto.getBudget()), dto.getBudgetType());
    }
}
