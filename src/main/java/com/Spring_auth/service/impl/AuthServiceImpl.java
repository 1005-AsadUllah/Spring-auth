package com.Spring_auth.service.impl;

import com.Spring_auth.dto.UserDto;
import com.Spring_auth.service.AuthService;
import com.Spring_auth.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    @Override
    public UserDto registerUser(UserDto userDto) {

        return userService.createUser(userDto);
    }
}
