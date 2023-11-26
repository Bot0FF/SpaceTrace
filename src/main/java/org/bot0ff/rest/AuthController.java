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
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
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
    public ResponseEntity<?> getAccessToken(@RequestBody JwtAuthRequest authRequest) throws Exception {
        try {
            authenticate(authRequest.getUsername(), authRequest.getPassword());
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

        String accessToken = jwtTokenUtil.generateAccessToken(userDetails);
        String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

        return ResponseEntity.ok(new JwtAuthResponse(authRequest.getUsername(), accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> getRefreshToken(@RequestBody JwtRefreshRequest jwtRefreshRequest) throws Exception {

        String username = jwtTokenUtil.getUsernameFromToken(jwtRefreshRequest.getRefreshToken());
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if(!jwtTokenUtil.validateToken(jwtRefreshRequest.getRefreshToken(), userDetails)) {
            return ResponseEntity.badRequest().body("Username not found");
        }

        String accessToken = jwtTokenUtil.generateAccessToken(userDetails);

        return ResponseEntity.ok(new JwtAuthResponse(username, accessToken, jwtRefreshRequest.getRefreshToken()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody JwtRegisterRequest registerRequest) throws Exception {
        if(userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exist");
        }

        User user = new User(null, registerRequest.getUsername(),
                passwordEncoder.encode(registerRequest.getPassword()),
                Role.USER, Status.ACTIVE);
        userRepository.save(user);

        User currentUser = userRepository.findByUsername(user.getUsername()).orElse(null);
        if(currentUser != null) {
            Player player = new Player(currentUser.getId(), user.getUsername(), LocationType.PLAIN, 5, 5);
            playerRepository.save(player);
        }
        else {
            userRepository.delete(user);
            return ResponseEntity.badRequest().body("Ошибка при создании игрока");
        }

        try {
            authenticate(registerRequest.getUsername(), registerRequest.getPassword());
        } catch (BadCredentialsException e) {
            throw new Exception("Некорректное имя или пароль", e);
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(registerRequest.getUsername());
        String token = jwtTokenUtil.generateAccessToken(userDetails);
        return ResponseEntity.ok(new JwtRegisterResponse(registerRequest.getUsername(), token));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() throws Exception {

         return ResponseEntity.ok("");
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
