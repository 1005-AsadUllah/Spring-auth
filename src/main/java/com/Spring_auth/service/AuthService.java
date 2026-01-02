package com.Spring_auth.service;

import com.Spring_auth.dto.UserDto;
import com.Spring_auth.enitity.Provider;
import com.Spring_auth.enitity.User;
import com.Spring_auth.exception.NotFoundException;
import com.Spring_auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AuthService {

    UserDto registerUser(UserDto userDto);

}
