package org.bot0ff.rest;

import lombok.RequiredArgsConstructor;
import org.bot0ff.dto.jwt.JwtRegisterRequest;
import org.bot0ff.dto.jwt.JwtRegisterResponse;
import org.bot0ff.entity.Role;
import org.bot0ff.entity.Status;
import org.bot0ff.entity.User;
import org.bot0ff.security.jwt.JwtTokenUtil;
import org.bot0ff.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RegistrationController {
    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody JwtRegisterRequest registerRequest) throws Exception {
        if(userService.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exist");
        }

        User user = new User(null, registerRequest.getUsername(),
                passwordEncoder.encode(registerRequest.getPassword()),
                Role.USER, Status.ACTIVE, 0, 0);

        userService.saveUser(user);

        try {
            authenticate(registerRequest.getUsername(), registerRequest.getPassword());
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(registerRequest.getUsername());
        String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtRegisterResponse(token));
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
