package org.bot0ff.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.config.jwt.JwtUtils;
import org.bot0ff.config.service.UserDetailsImpl;
import org.bot0ff.dto.auth.*;
import org.bot0ff.entity.*;
import org.bot0ff.repository.LocationRepository;
import org.bot0ff.repository.PlayerRepository;
import org.bot0ff.repository.UserRepository;
import org.bot0ff.service.MainService;
import org.bot0ff.util.Constants;
import org.bot0ff.util.ResponseBuilder;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    protected final LocationRepository locationRepository;
    private final MainService mainService;

    //авторизация
    @PostMapping("/login")
    public ResponseEntity<?> getAccessToken(@Valid @RequestBody AuthRequest authRequest) {
         Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
         SecurityContextHolder.getContext().setAuthentication(authentication);
         UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
         ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
         var response = mainService.getPlayerState(userDetails.getUsername());

         return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                 .body(response);
    }

    //регистрация
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        if(userRepository.existsByUsername(registerRequest.getUsername())) {
            var response = ResponseBuilder.builder()
                    .httpStatus(HttpStatus.NO_CONTENT)
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

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(user.getUsername());
    }

    //настройка нового пользователя
    @PostMapping("/setting")
    public ResponseEntity<?> settingUser(@RequestBody SettingRequest settingRequest) {
        String username = "admin";
        var user = userRepository.findByUsername(username).orElse(null);
        if(user == null) {
            var response = ResponseBuilder.builder()
                    .httpStatus(HttpStatus.NO_CONTENT)
                    .build();
            return ResponseEntity.ok(response);
        }
        if(playerRepository.existsByName(user.getUsername())) {
            var response = ResponseBuilder.builder()
                    .httpStatus(HttpStatus.NO_CONTENT)
                    .build();
            return ResponseEntity.badRequest().body(response);
        }
        var locationId = Long.parseLong("" + Constants.START_POS_X + Constants.START_POS_Y);
        var location = locationRepository.findById(locationId).orElse(null);
        if(location == null) {
            var response = ResponseBuilder.builder()
                    .httpStatus(HttpStatus.NO_CONTENT)
                    .build();
            log.info("Не найдена location в БД по запросу locationId: {}", locationId);
            return ResponseEntity.ok(response);
        }
        var player = new Player();
        player.setName(user.getUsername());
        player.setX(Constants.START_POS_X);
        player.setY(Constants.START_POS_Y);
        player.setHp(Constants.START_HP);
        player.setMana(Constants.START_MANA);
        player.setLocation(location);
        player.setStatus(Status.ACTIVE);
        playerRepository.save(player);
        var response = mainService.getPlayerState(username);
        return ResponseEntity.ok(response);
    }
}
