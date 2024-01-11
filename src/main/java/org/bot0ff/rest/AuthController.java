package org.bot0ff.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.config.jwt.JwtUtils;
import org.bot0ff.config.service.UserDetailsImpl;
import org.bot0ff.dto.Response;
import org.bot0ff.dto.auth.*;
import org.bot0ff.entity.*;
import org.bot0ff.entity.enums.Role;
import org.bot0ff.repository.UserRepository;
import org.bot0ff.service.MainService;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@CrossOrigin
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final MainService mainService;

    //авторизация
    @PostMapping("/login")
    public ResponseEntity<?> getAccessToken(@Valid @RequestBody AuthRequest authRequest) {
         Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
         SecurityContextHolder.getContext().setAuthentication(authentication);
         UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
         ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
         var response = mainService.getUserState(userDetails.getUsername());

         return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                 .body(response);
    }

    //регистрация
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        if(registerRequest.getUsername().startsWith("*")) {
            var response = Response.builder()
                    .info("Имя не должно начинаться с символов '*'")
                    .status(0)
                    .build();
            return ResponseEntity.badRequest().body(response);
        }
        if(userRepository.existsByUsername(registerRequest.getUsername())) {
            var response = Response.builder()
                    .info("Игрок с таким именем уже зарегистрирован")
                    .status(0)
                    .build();
            return ResponseEntity.badRequest().body(response);
        }
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setRole(List.of(Role.USER));
        userRepository.save(user);

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(registerRequest.getUsername(), registerRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        var response = Map.of("username", userDetails.getUsername(),
                "status", "OK");

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(response);
    }

    //новости
    @GetMapping("/news")
    public ResponseEntity<?> getNews() {
        return ResponseEntity.ok().body("");
    }
}
