package org.bseber.haushalt.tags;

import org.springframework.stereotype.Component;

import static org.springframework.util.StringUtils.hasText;

@Component
class NewTagBucketValidator extends AbstractTagBucketValidator {

    @Override
    protected boolean tagShouldBeValidated(TagDto tagDto) {
        // validate if at least one input field has been filled
        // (selects are not of interest because they have a default value)
        return hasText(tagDto.getName()) || hasText(tagDto.getDescription()) || tagDto.getBudget() != null;
    }
}
