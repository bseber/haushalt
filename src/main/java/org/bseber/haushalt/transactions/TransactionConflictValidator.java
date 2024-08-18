package org.bseber.haushalt.transactions;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.Set;

class TransactionConflictValidator implements ConstraintValidator<TransactionConflictConstraintWhenImported, TransactionConflictDto> {

    private final Validator validator;

    TransactionConflictValidator(Validator validator) {
        this.validator = validator;
    }

    @Override
    public boolean isValid(TransactionConflictDto value, ConstraintValidatorContext context) {
        if (!value.isImportMe()) {
            return true;
        }

        final TransactionImportDto transaction = value.getTransaction();
        if (transaction == null) {
            context
                .buildConstraintViolationWithTemplate("{jakarta.validation.constraints.NotNull.message}")
                .addPropertyNode("transaction")
                .addConstraintViolation();
            return false;
        }

        final Set<ConstraintViolation<TransactionImportDto>> violations = validator.validate(transaction);
        for (ConstraintViolation<TransactionImportDto> violation : violations) {
            final String messageTemplate = violation.getMessageTemplate();
            final String propertyPath = violation.getPropertyPath().toString();
            context
                .buildConstraintViolationWithTemplate(messageTemplate)
                .addPropertyNode("transaction." + propertyPath)
                .addConstraintViolation();
        }

        return violations.isEmpty();
    }
}
