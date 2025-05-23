package team.startup.expo.global.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import team.startup.expo.domain.auth.presentation.dto.response.TokenResponseDto;
import team.startup.expo.global.security.exception.ExpiredTokenException;
import team.startup.expo.global.security.exception.InvalidTokenException;

import java.time.LocalDateTime;
import java.util.Date;


@Component
@RequiredArgsConstructor
public class JwtProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private static final long ACCESS_TOKEN_TIME = 60L * 60 * 24;
    public static final long REFRESH_TOKEN_TIME = 60L * 60 * 24 * 7;

    private final JwtProperties jwtProperties;

    private Key getSignInAccessKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getAccessSecret().getBytes(StandardCharsets.UTF_8));
    }

    private Key getSignInSecretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getRefreshSecret().getBytes(StandardCharsets.UTF_8));
    }

    public TokenResponseDto generateTokenDto(String email) {
        return TokenResponseDto.builder()
                .accessToken(generateAccessToken(email))
                .refreshToken(generateRefreshToken(email))
                .accessTokenExpiresIn(LocalDateTime.now().plusSeconds(ACCESS_TOKEN_TIME))
                .refreshTokenExpiresIn(LocalDateTime.now().plusSeconds(REFRESH_TOKEN_TIME))
                .build();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSignInAccessKey()).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new  ExpiredTokenException();
        } catch (Exception e) {
            throw new InvalidTokenException();
        }
    }

    public String generateAccessToken(String email) {
        Date accessTokenExpiresIn = new Date(System.currentTimeMillis() + ACCESS_TOKEN_TIME*1000);

        return Jwts.builder()
                .setSubject(email)
                .claim(AUTHORITIES_KEY, "JWT")
                .setIssuedAt(new Date())
                .setExpiration(accessTokenExpiresIn)
                .signWith(getSignInAccessKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String email) {
        Date refreshTokenExpiresIn = new Date(System.currentTimeMillis() + REFRESH_TOKEN_TIME*1000);

        return Jwts.builder()
                .setSubject(email)
                .setExpiration(refreshTokenExpiresIn)
                .signWith(getSignInSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
