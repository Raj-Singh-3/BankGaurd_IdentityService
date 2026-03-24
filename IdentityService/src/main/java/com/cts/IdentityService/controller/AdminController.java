package com.cts.IdentityService.controller;


import com.cts.IdentityService.entity.User;
import com.cts.IdentityService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepo;

    // Only ADMIN can view all users
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    // ADMIN can delete user
    @DeleteMapping("/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(@PathVariable String id) {
        userRepo.deleteById(id);
        return "User deleted";
    }
}
