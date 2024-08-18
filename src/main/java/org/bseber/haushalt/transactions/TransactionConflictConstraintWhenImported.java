package org.bseber.haushalt.transactions;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = TransactionConflictValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface TransactionConflictConstraintWhenImported {

    String message() default "";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
