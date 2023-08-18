package com.banking.springboot.util.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;

public class FieldsMatchImpl implements ConstraintValidator<FieldsMatch, Object> {
    private String fieldOne;
    private String fieldTwo;

    @Override
    public void initialize(FieldsMatch constraintAnnotation) {
        fieldOne = constraintAnnotation.fieldOne();
        fieldTwo = constraintAnnotation.fieldTwo();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        try {
            final String fieldOneValue = BeanUtils.getProperty(value, fieldOne);
            final String fieldTwoValue = BeanUtils.getProperty(value, fieldTwo);

            if ((fieldOneValue == null && fieldTwoValue == null) || (fieldOneValue != null && fieldOneValue.equals(fieldTwoValue))) {
                return true;
            }
        } catch (Exception e) {
            // do nothing
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode(fieldOne)
                .addConstraintViolation();

        return false;
    }
}
