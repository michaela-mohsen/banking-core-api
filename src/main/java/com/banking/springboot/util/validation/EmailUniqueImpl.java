package com.banking.springboot.util.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.banking.springboot.auth.User;
import com.banking.springboot.auth.UserRepository;

public class EmailUniqueImpl implements ConstraintValidator<EmailUnique, String> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(EmailUnique constraintAnnotation) {
        // initializes Email Unique Validation
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(value)) {
            return true;
        }
        User user = userRepository.findByEmail(value);
        return (user == null);
    }
}
