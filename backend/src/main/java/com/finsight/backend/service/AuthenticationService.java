package com.finsight.backend.service;

import com.finsight.backend.dto.AuthResponse;
import com.finsight.backend.dto.LoginRequest;
import com.finsight.backend.dto.RegisterRequest;
import com.finsight.backend.dto.UserResponse;
import com.finsight.backend.entity.User;
import com.finsight.backend.enums.Currency;
import com.finsight.backend.enums.Language;
import com.finsight.backend.enums.Role;
import com.finsight.backend.exception.UserAlreadyExistsException;
import com.finsight.backend.repository.UserRepository;
import com.finsight.backend.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public UserResponse register(RegisterRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new UserAlreadyExistsException("Email already exists.");
        }
        String encryptedPassword= passwordEncoder.encode(request.getPassword());

        User user= User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(encryptedPassword)
                .currency(Currency.INR)
                .role(Role.USER)
                .country("India")
                .preferredLanguage(Language.ENGLISH)
                .timezone("Asia/Kolkata")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User savedUser=userRepository.save(user);

        return UserResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .currency(savedUser.getCurrency())
                .country(savedUser.getCountry())
                .preferredLanguage(savedUser.getPreferredLanguage())
                .timezone(savedUser.getTimezone())
                .profilePictureUrl(savedUser.getProfilePictureUrl())
                .build();
    }

    public AuthResponse login(LoginRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            System.out.println("Authentication SUCCESS");
        } catch (Exception e) {
            System.out.println("Authentication FAILED");
            e.printStackTrace();
            throw e;
        }

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        String token = jwtService.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
