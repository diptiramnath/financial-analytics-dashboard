package com.finsight.backend.controller;

import com.finsight.backend.dto.AuthResponse;
import com.finsight.backend.dto.LoginRequest;
import com.finsight.backend.dto.RegisterRequest;
import com.finsight.backend.dto.UserResponse;
import com.finsight.backend.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request){
        UserResponse response=authenticationService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        System.out.println("LOGIN ENDPOINT HIT");
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping("/test")
    public String test() {
        return "working";
    }
}
