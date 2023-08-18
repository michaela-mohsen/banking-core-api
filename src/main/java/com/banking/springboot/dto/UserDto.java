package com.banking.springboot.dto;

import com.banking.springboot.util.validation.EmailUnique;
import lombok.Data;
import lombok.Generated;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
@Generated
public class UserDto {
    private Integer id;

    @NotEmpty(message = "Email is required.")
    @Length(max = 256, message = "Email must be less than 256 characters.")
    @EmailUnique
    private String email;

    @NotEmpty(message = "Password is required.")
    @Pattern(regexp = "^[a-zA-Z0-9!@#]+$", message = "Password can only contain lowercase, uppercase, and special characters")
    @Length(min = 8, max = 25, message = "Password must be between 8 and 25 characters.")
    private String password;

    @NotEmpty(message = "Confirm password is required.")
    private String confirmPassword;

    @NotEmpty(message = "First name is required.")
    @Length(max = 45, message = "First name must be less than 45 characters.")
    private String firstName;

    @NotEmpty(message = "Last name is required.")
    @Length(max = 45, message = "Last name must be less than 45 characters.")
    private String lastName;

    @NotEmpty(message = "Title is required.")
    @Length(max = 45, message = "Title must be less than 45 characters.")
    private String title;

    private String avatar;
}
