package com.formation.blog_security.controllers;

import com.formation.blog_security.dtos.AuthenticationDto;
import com.formation.blog_security.entities.UserEntity;
import com.formation.blog_security.exceptions.UserExistingException;
import com.formation.blog_security.jwt.JwtService;
import com.formation.blog_security.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final BCryptPasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, BCryptPasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserEntity> createNewUser(@Valid @RequestBody UserEntity userEntity) throws UserExistingException {
        Optional<UserEntity> userOpt = userService.findByEmail(userEntity.getEmail());
        if (userOpt.isPresent()) {
            throw new UserExistingException("Un utilisateur avec cet email existe déjà!");
        }
        userService.register(userEntity);
        return ResponseEntity.ok(userEntity);
    }

    @PostMapping("/signin")
    public ResponseEntity authenticateUser(@Valid @RequestBody AuthenticationDto authenticationDto) {
        final Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationDto.email(), authenticationDto.password())
        );

        if (authenticate.isAuthenticated()) {
            return ResponseEntity.ok(jwtService.generateToken(authenticationDto.email()));
        }

        return new ResponseEntity(Map.of("errors", "Unauthorized"), HttpStatus.UNAUTHORIZED);
    }
}
