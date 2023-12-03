package org.bot0ff.rest;

import lombok.RequiredArgsConstructor;
import org.bot0ff.dto.jwt.*;
import org.bot0ff.entity.Player;
import org.bot0ff.repository.PlayerRepository;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Validated
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final PlayerRepository playerRepository;

    //главная страница, доступная всем (новости и пр.)
    @GetMapping("/hi")
    public ResponseEntity<?> hi() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("Добро пожаловать в VezLand!");
    }

    @PostMapping("/auth")
    public ResponseEntity<?> getAccessToken(@AuthenticationPrincipal UserDetails user) {
        Player player = playerRepository.findByName(user.getUsername()).orElse(null);
         if(player == null) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Пользователь не найден"));
         }

         return ResponseEntity.ok()
                 .contentType(MediaType.APPLICATION_JSON)
                 .body(new JwtAuthResponse(player));
    }
//    @PostMapping("/register")
//    public ResponseEntity<?> registerUser(@Valid @RequestBody JwtRegisterRequest registerRequest) {
//
//        if(userRepository.existsByUsername(registerRequest.getUsername())) {
//            return ResponseEntity.badRequest().body(Map.of("error", "Пользователь с таким именем уже зарегистрирован"));
//        }
//
//        //сохранение в бд нового пользователя
//        User user = new User(null, registerRequest.getUsername(),
//                passwordEncoder.encode(registerRequest.getPassword()),
//                registerRequest.getEmail(), "refreshToken", Role.USER, Status.ACTIVE);
//        userRepository.save(user);
//
//        UserDetails userDetails = userDetailsService.loadUserByUsername(registerRequest.getUsername());
//        String accessToken = jwtTokenUtil.generateAccessToken(userDetails);
//        String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);
//
//        //сохранение refreshToken user
//        User currentUser = userRepository.findByUsername(user.getUsername()).orElse(null);
//        Player player;
//
//        if(currentUser != null) {
//            userRepository.setUserRefreshToken(refreshToken, currentUser.getUsername());
//            player = new Player(currentUser.getId(), user.getUsername(), LocationType.PLAIN, 5, 5);
//            playerRepository.save(player);
//        }
//        else {
//            userRepository.delete(user);
//            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка регистрации нового пользователя"));
//        }
//
//        authenticate(registerRequest.getUsername(), registerRequest.getPassword());
//
//        HttpCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
//                .httpOnly(true)
//                .build();
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.SET_COOKIE, cookie.toString())
//                .body(new JwtRegisterResponse(player, accessToken));
//    }
}
