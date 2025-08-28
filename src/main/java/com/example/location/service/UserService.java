package com.example.location.service;
import java.util.random.RandomGenerator;

import org.springframework.stereotype.Service;

import com.example.location.entity.UserEntity;
import com.example.location.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    public boolean createUser(UserEntity user) {
        userRepository.save(user);
        return true; 
    }
}
