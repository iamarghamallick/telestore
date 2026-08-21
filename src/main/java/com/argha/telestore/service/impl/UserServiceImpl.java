package com.argha.telestore.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.argha.telestore.dto.user.UserResponse;
import com.argha.telestore.dto.user.UpdateUserRequest;
import com.argha.telestore.entity.User;
import com.argha.telestore.exception.UserNotFoundException;
import com.argha.telestore.repository.UserRepository;
import com.argha.telestore.security.CustomUserDetails;
import com.argha.telestore.service.UserService;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;

    public UserServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return new CustomUserDetails(user);
    }

    public UserResponse getMe(String userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        UserResponse userResponse = new UserResponse();

        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());

        return userResponse;
    }

    public UserResponse updateUser(String email, UpdateUserRequest request) {
        Optional<User> userOpt = userRepo.findByEmail(email);

        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        User user = userOpt.get();

        if (request.getName() != null) {
            user.setName(request.getName());
        }

        User updatedUser = userRepo.save(user);

        UserResponse userResponse = new UserResponse();

        userResponse.setId(updatedUser.getId());
        userResponse.setName(updatedUser.getName());
        userResponse.setEmail(updatedUser.getEmail());
        userResponse.setCreatedAt(updatedUser.getCreatedAt());
        userResponse.setUpdatedAt(updatedUser.getUpdatedAt());

        return userResponse;
    }
}