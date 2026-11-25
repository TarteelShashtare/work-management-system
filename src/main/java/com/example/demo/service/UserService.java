
package com.example.demo.service;

import com.example.demo.dto.CreateUserRequest;
import com.example.demo.dto.UpdateUserRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Create User
    public UserResponse createUser(CreateUserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());
        user.setActive(true);

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    // Get All Users
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get User By ID
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id));

        return mapToResponse(user);
    }

    // Update User
    public UserResponse updateUser(Long id, UpdateUserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id));

        if (userRepository.existsByEmail(request.getEmail())
                && !user.getEmail().equals(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());

        User updatedUser = userRepository.save(user);

        return mapToResponse(updatedUser);
    }

    // Deactivate User
    public void deactivateUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id));

        user.setActive(false);

        userRepository.save(user);
    }

    // Convert User Entity to UserResponse DTO
    private UserResponse mapToResponse(User user) {

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setActive(user.getActive());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        return response;
    }
}
