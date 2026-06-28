package com.upm.taskforce.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class UserRegistrationDto {

    @NotEmpty(message = "{NotEmpty.userRegistrationDto.username}")
    @Size(min = 3, max = 50, message = "{Size.userRegistrationDto.username}")
    private String username;

    @NotEmpty(message = "{NotEmpty.userRegistrationDto.password}")
    @Size(min = 8, message = "{Size.userRegistrationDto.password}")
    private String password;

    @NotEmpty(message = "{NotEmpty.userRegistrationDto.confirmPassword}")
    private String confirmPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}