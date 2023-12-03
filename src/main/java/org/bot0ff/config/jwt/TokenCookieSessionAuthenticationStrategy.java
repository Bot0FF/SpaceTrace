package org.bot0ff.config.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.function.Function;

public class TokenCookieSessionAuthenticationStrategy implements SessionAuthenticationStrategy {
    //TokenCookieFactory - фабрика создания токенов
    private Function<Authentication, Token> tokenCookieFactory = new TokenCookieFactory();
    //TokenCookieJweStringSerializer - преобразовывает токен в строку
    private Function<Token, String> tokenStringSerializer = Objects::toString;

    @Override
    public void onAuthentication(Authentication authentication, HttpServletRequest request,
                                 HttpServletResponse response) throws SessionAuthenticationException {
        //проверка, чтобы каждый раз не создавался токен на успешную аутентификацию
        if(authentication instanceof UsernamePasswordAuthenticationToken) {
            //в случае успешной аутентификации создаем токен
            var token = this.tokenCookieFactory.apply(authentication);
            //преобразовываем токен в строку
            var tokenString = this.tokenStringSerializer.apply(token);

            //сохраняем куку
            var cookie = new Cookie("__Host-auth-token", tokenString);
            cookie.setPath("/");
            cookie.setDomain(null);
            cookie.setHttpOnly(true);
            cookie.setMaxAge((int) ChronoUnit.SECONDS.between(Instant.now(), token.expiresAt()));

            response.addCookie(cookie);
        }
    }

    public void setTokenCookieFactory(Function<Authentication, Token> tokenCookieFactory) {
        this.tokenCookieFactory = tokenCookieFactory;
    }

    public void setTokenStringSerializer(Function<Token, String> tokenStringSerializer) {
        this.tokenStringSerializer = tokenStringSerializer;
    }
}
