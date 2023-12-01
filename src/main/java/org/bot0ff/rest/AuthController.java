package org.bot0ff.rest;

import lombok.RequiredArgsConstructor;
import org.bot0ff.dto.jwt.*;
import org.bot0ff.entity.Player;
import org.bot0ff.entity.Role;
import org.bot0ff.entity.Status;
import org.bot0ff.entity.User;
import org.bot0ff.repository.PlayerRepository;
import org.bot0ff.repository.UserRepository;
import org.bot0ff.security.jwt.JwtTokenUtil;
import org.bot0ff.world.LocationType;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.Objects;

@Validated
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/auth")
    public ResponseEntity<?> getAccessToken(@Valid @RequestBody JwtAuthRequest authRequest) {

        authenticate(authRequest.getUsername(), authRequest.getPassword());
        try {
             UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
             String accessToken = jwtTokenUtil.generateAccessToken(userDetails);
             String refreshToken = Objects.requireNonNull(userRepository.findRefreshTokenByUsername(authRequest.getUsername()).orElse(null)).getRefreshToken();

             if(StringUtils.isEmpty(refreshToken) && !jwtTokenUtil.validateToken(refreshToken, userDetails)) {
                 refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);
                 userRepository.setUserRefreshToken(refreshToken, authRequest.getUsername());
             }

             Player player = playerRepository.findByName(authRequest.getUsername()).orElse(null);
             if(player == null) {
                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Пользователь не найден"));
             }
             else {
                 HttpCookie cookie = ResponseCookie
                         .from("refreshToken", refreshToken)
                         .httpOnly(true)
                         .build();

                 return ResponseEntity.ok()
                         .header(HttpHeaders.SET_COOKIE, cookie.toString())
                         .body(new JwtAuthResponse(player, accessToken));
             }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Что-то пошло не так"));
        }
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> getRefreshToken(@CookieValue(value = "refreshToken", defaultValue = "Atta") String refreshToken) {
        if(StringUtils.isEmpty(refreshToken)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Нужна авторизация"));
        }
        String username = jwtTokenUtil.getUsernameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if(!jwtTokenUtil.validateToken(refreshToken, userDetails)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Нужна авторизация"));
        }

        Player player = playerRepository.findByName(username).orElse(null);
        if(player == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Пользователь не найден"));
        }

        String accessToken = jwtTokenUtil.generateAccessToken(userDetails);

        return ResponseEntity.ok(new JwtRefreshResponse(player, accessToken));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody JwtRegisterRequest registerRequest) {

        if(userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Пользователь с таким именем уже зарегистрирован"));
        }

        //сохранение в бд нового пользователя
        User user = new User(null, registerRequest.getUsername(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getEmail(), "refreshToken", Role.USER, Status.ACTIVE);
        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(registerRequest.getUsername());
        String accessToken = jwtTokenUtil.generateAccessToken(userDetails);
        String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

        //сохранение refreshToken user
        User currentUser = userRepository.findByUsername(user.getUsername()).orElse(null);
        Player player;

        if(currentUser != null) {
            userRepository.setUserRefreshToken(refreshToken, currentUser.getUsername());
            player = new Player(currentUser.getId(), user.getUsername(), LocationType.PLAIN, 5, 5);
            playerRepository.save(player);
        }
        else {
            userRepository.delete(user);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка регистрации нового пользователя"));
        }

        authenticate(registerRequest.getUsername(), registerRequest.getPassword());

        HttpCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new JwtRegisterResponse(player, accessToken));
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
