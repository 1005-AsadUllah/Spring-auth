package com.Spring_auth.service.impl;

import com.Spring_auth.dto.UserDto;
import com.Spring_auth.enitity.Provider;
import com.Spring_auth.enitity.User;
import com.Spring_auth.exception.NotFoundException;
import com.Spring_auth.repository.UserRepository;
import com.Spring_auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("User already exists");
        }
        User user = modelMapper.map(userDto, User.class);
        user.setProvider(userDto.getProvider() != null ? userDto.getProvider() : Provider.LOCAL);

        //TODO: role assignment
        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with given email id"));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found with given id"));
        if (userDto.getUsername() != null) user.setUsername(userDto.getUsername());
        if (userDto.getImageUrl() != null) user.setEmail(userDto.getImageUrl());
        if (userDto.getProvider() != null) user.setProvider(userDto.getProvider());
        if (userDto.getImageUrl() != null) user.setImageUrl(userDto.getImageUrl());
        //TODO: change password update logic
        if (userDto.getPassword() != null) user.setPassword(userDto.getPassword());
        user.setEnabled(userDto.isEnabled());
        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found with id " + id));
        userRepository.deleteById(id);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found with id " + id));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository
                .findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .toList();
    }
}
