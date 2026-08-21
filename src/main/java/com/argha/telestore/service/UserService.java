package com.argha.telestore.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.argha.telestore.dto.user.UserResponse;
import com.argha.telestore.dto.user.UpdateUserRequest;

public interface UserService extends UserDetailsService {

    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    UserResponse getMe(String userId);

    UserResponse updateUser(String email, UpdateUserRequest request);
}
