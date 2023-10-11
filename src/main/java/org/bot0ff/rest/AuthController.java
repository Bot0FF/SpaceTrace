package org.bot0ff.rest;

import lombok.RequiredArgsConstructor;
import org.bot0ff.dto.jwt.JwtAuthRequest;
import org.bot0ff.dto.jwt.JwtAuthResponse;
import org.bot0ff.security.jwt.JwtTokenUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

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

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
