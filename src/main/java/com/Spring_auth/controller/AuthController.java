package com.Spring_auth.controller;

import com.Spring_auth.dto.LoginRequest;
import com.Spring_auth.dto.TokenResponse;
import com.Spring_auth.dto.UserDto;
import com.Spring_auth.enitity.User;
import com.Spring_auth.repository.UserRepository;
import com.Spring_auth.security.JwtService;
import com.Spring_auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;

    @PostMapping("register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto userDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService
                        .registerUser(userDto));
    }

    @PostMapping("login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest){

        //Authenticate the user
        Authentication authentication =  authenticate(loginRequest);
        User user = userRepository.findByEmail(loginRequest.username()).orElseThrow(() -> new BadCredentialsException("Invalid username and password"));
        if(!user.isEnabled()){
            throw new DisabledException("User is not enabled");
        }

        // Generate JWT token
        String accessToken = jwtService.generateAccessToken(user);
        TokenResponse tokenResponse =  TokenResponse.of(
                accessToken,
                null,
                jwtService.getExpiration(),
                modelMapper.map(user, UserDto.class)
        );
        return ResponseEntity.ok(tokenResponse);

    }

    private Authentication authenticate(LoginRequest loginRequest) {
        try{
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
        }catch (Exception e){
            throw new BadCredentialsException("Invalid username and password");
        }
    }
}
