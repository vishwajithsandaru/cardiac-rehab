package com.dexter.fyp.backend.dto;

import com.dexter.fyp.backend.enums.Role;

import lombok.Data;

@Data
public class UserDetailsDto {
    
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;

}
