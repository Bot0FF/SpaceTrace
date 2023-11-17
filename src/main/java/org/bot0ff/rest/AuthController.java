package org.bot0ff.rest;

import lombok.RequiredArgsConstructor;
import org.bot0ff.dto.jwt.JwtAuthRequest;
import org.bot0ff.dto.jwt.JwtAuthResponse;
import org.bot0ff.dto.jwt.JwtRegisterRequest;
import org.bot0ff.dto.jwt.JwtRegisterResponse;
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

@CrossOrigin
@RestController()
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtAuthRequest authRequest) throws Exception {
        try {
            authenticate(authRequest.getUsername(), authRequest.getPassword());
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

        String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtAuthResponse(token));
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
        String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtRegisterResponse(token));
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
