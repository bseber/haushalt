package org.bseber.haushalt.tags;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import static org.springframework.validation.ValidationUtils.rejectIfEmpty;

@Component
class UpdateTagBucketValidator extends AbstractTagBucketValidator {

    private static final String NOT_NULL = "jakarta.validation.constraints.NotNull.message";

    @Override
    protected boolean tagShouldBeValidated(TagDto tagDto) {
        return true;
    }

    @Override
    protected void validateTagId(TagDto tagDto, Errors errors) {
        rejectIfEmpty(errors, "id", NOT_NULL);
    }
}
