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
import java.util.Optional;
import java.util.stream.IntStream;

import static org.springframework.util.StringUtils.hasText;

@Controller
@RequestMapping("/tags/new")
class NewTagController {

    private final TagService tagService;
    private final NewTagBucketValidator validator;

    NewTagController(TagService tagService, NewTagBucketValidator validator) {
        this.tagService = tagService;
        this.validator = validator;
    }

    @GetMapping
    public String tags(Model model, HttpServletRequest request) {

        final Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);

        if (inputFlashMap == null || !inputFlashMap.containsKey("newTags")) {
            final List<TagDto> newTagDtos = IntStream.range(0, 5).mapToObj(i -> new TagDto()).toList();
            model.addAttribute("newTags", new TagBucketDto(newTagDtos));
        }

        return "tags/new";
    }

    @PostMapping
    public String newTags(@ModelAttribute("newTags") TagBucketDto tagBucket, Errors errors,
                          RedirectAttributes redirectAttributes) {

        validator.validate(tagBucket, errors);
        if (errors.hasErrors()) {
            redirectAttributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "newTags", errors);
            redirectAttributes.addFlashAttribute("newTags", tagBucket);
            return "redirect:/tags/new";
        } else {
            final List<NewTag> newTags = tagBucket.getTags().stream().map(NewTagController::toNewTag).filter(Optional::isPresent).map(Optional::get).toList();
            if (newTags.isEmpty()) {
                return "redirect:/tags/new";
            } else {
                tagService.createNewTag(newTags);
                redirectAttributes.addFlashAttribute("newTagsCreated", true);
                return "redirect:/tags";
            }
        }
    }

    private static Optional<NewTag> toNewTag(TagDto dto) {
        if (!(hasText(dto.getName()) || hasText(dto.getDescription()))) {
            return Optional.empty();
        }
        final TagName name = TagName.sanitized(dto.getName());
        final TagDescription description = TagDescription.sanitized(dto.getDescription());
        return Optional.of(new NewTag(name, description, dto.getColor(), budget(dto)));
    }

    private static Budget budget(TagDto dto) {
        return dto.getBudget() == null ? null : new Budget(Money.ofEUR(dto.getBudget()), dto.getBudgetType());
    }
}
