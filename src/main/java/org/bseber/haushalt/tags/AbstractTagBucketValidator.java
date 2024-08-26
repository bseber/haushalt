package org.bseber.haushalt.tags;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.springframework.validation.ValidationUtils.rejectIfEmpty;
import static org.springframework.validation.ValidationUtils.rejectIfEmptyOrWhitespace;

abstract class AbstractTagBucketValidator implements Validator {

    private static final String NOT_NULL = "jakarta.validation.constraints.NotNull.message";
    private static final String NOT_EMPTY = "jakarta.validation.constraints.NotEmpty.message";
    private static final String POSITIVE_OR_ZERO = "jakarta.validation.constraints.PositiveOrZero.message";

    @Override
    public boolean supports(Class<?> clazz) {
        return TagBucketDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        final TagBucketDto bucket = (TagBucketDto) target;

        if (bucket.getTags() == null) {
            errors.rejectValue("tags", NOT_NULL);
        } else if (bucket.getTags().isEmpty()) {
            errors.rejectValue("tags", NOT_EMPTY);
        } else {
            for (int i = 0; i < bucket.getTags().size(); i++) {
                errors.pushNestedPath("tags[" + i + "]");
                validateTag(bucket.getTags().get(i), errors);
                errors.popNestedPath();
            }
        }
    }

    private void validateTag(TagDto tag, Errors errors) {
        if (tagShouldBeValidated(tag)) {
            validateTagId(tag, errors);
            rejectIfEmptyOrWhitespace(errors, "name", NOT_EMPTY);
            rejectIfEmpty(errors, "color", NOT_NULL);
            validateBudget(tag, errors);
        }
    }

    private void validateBudget(TagDto tag, Errors errors) {
        if (tag.getBudget() != null && tag.getBudget().signum() == -1) {
            errors.rejectValue("budget", POSITIVE_OR_ZERO);
        }
    }

    /**
     * @param tagDto tag dto that should/not be validated
     * @return {@code true} when the dto has to be validated, {@code false} otherwise
     */
    protected abstract boolean tagShouldBeValidated(TagDto tagDto);

    /**
     * Override to valid {@linkplain TagDto#getId() id}. Default implementation does nothing.
     *
     * @param tagDto tag dto to validate
     * @param errors contextual state about the validation process
     */
    protected void validateTagId(TagDto tagDto, Errors errors) {
    }
}
