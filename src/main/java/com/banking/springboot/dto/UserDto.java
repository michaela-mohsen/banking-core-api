package com.banking.springboot.dto;

import com.banking.springboot.util.validation.EmailUnique;
import com.banking.springboot.util.validation.FieldsMatch;
import lombok.Data;
import lombok.Generated;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
@Generated
@FieldsMatch(fieldOne = "password", fieldTwo = "confirmPassword", message = "Password fields must match.")
public class UserDto {
    private Integer id;

    @NotEmpty(message = "Email is required.")
    @EmailUnique(message = "Email already exists. Please login or use another email.")
    @Pattern(regexp = "^[a-zA-Z0-9.*]+@[a-zA-Z]+.[a-zA-Z]+$", message = "Email must be in the format of example123@website.com.")
    private String email;

    @Pattern(regexp = "^[a-zA-Z0-9!@#]+$", message = "Password can only contain lowercase, uppercase, and special characters.")
    @Length(min = 8, max = 25, message = "Password must be between 8 and 25 characters.")
    private String password;

    @NotEmpty(message = "Confirm password is required.")
    private String confirmPassword;

    @Length(min = 2, max = 45, message = "First name must be between 2 and 45 characters.")
    private String firstName;

    @Length(min = 2, max = 45, message = "Last name must be between 2 and 45 characters.")
    private String lastName;

    @NotEmpty(message = "Title is required.")
    private String title;

    private String avatar;

    @NotEmpty(message = "Branch is required.")
    private String branch;
}
