package com.Spring_auth.service;

import com.Spring_auth.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto getUserByEmail(String email);

    UserDto updateUser(Long id,UserDto userDto);

    void deleteUserById(Long id);

    UserDto getUserById(Long id);

    List<UserDto> getAllUsers();

}
