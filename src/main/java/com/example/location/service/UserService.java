package com.example.location.service;

import org.springframework.stereotype.Service;
import com.example.location.entity.UserEntity;
import com.example.location.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.location.cache.LocationCache;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LocationCache locationCache;

    public boolean createUser(UserEntity user) {

        userRepository.save(user);
        locationCache.mapEmailToUserId(user.getEmail(), user.getId());
        return true; 
    }

}
