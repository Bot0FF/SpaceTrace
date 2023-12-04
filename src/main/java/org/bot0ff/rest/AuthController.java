package org.bot0ff.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bot0ff.config.jwt.JwtUtils;
import org.bot0ff.config.service.UserDetailsImpl;
import org.bot0ff.dto.jwt.*;
import org.bot0ff.entity.Player;
import org.bot0ff.entity.Role;
import org.bot0ff.entity.Status;
import org.bot0ff.entity.User;
import org.bot0ff.repository.PlayerRepository;
import org.bot0ff.repository.UserRepository;
import org.bot0ff.world.LocationType;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;

    @GetMapping("/")
    public ResponseEntity<?> getAccessToken(@AuthenticationPrincipal UserDetails userDetails) {
        Player player = playerRepository.findByName(userDetails.getUsername()).orElse(null);
        if(player == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Пользователь не найден"));
        }

        return ResponseEntity.ok().body(new JwtAuthResponse(player));
    }

    @PostMapping("/auth")
    public ResponseEntity<?> getAccessToken(@Valid @RequestBody JwtAuthRequest authRequest) {
         Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
         SecurityContextHolder.getContext().setAuthentication(authentication);
         UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
         ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

         Player player = playerRepository.findByName(userDetails.getUsername()).orElse(null);
         if(player == null) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Пользователь не найден"));
         }

         return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                 .body(new JwtAuthResponse(player));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody JwtRegisterRequest registerRequest) {

        if(userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Пользователь с таким именем уже зарегистрирован"));
        }

        //сохранение в бд нового пользователя
        User user = new User(null, registerRequest.getUsername(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getEmail(), List.of(Role.USER), Status.ACTIVE);
        userRepository.save(user);

        //сохранение refreshToken user
        User currentUser = userRepository.findByUsername(user.getUsername()).orElse(null);
        if(currentUser == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка регистрации"));
        }
        Player player = new Player(currentUser.getId(), user.getUsername(), LocationType.PLAIN, 5, 5);
        playerRepository.save(player);

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(registerRequest.getUsername(), registerRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new JwtAuthResponse(player));
    }
}
