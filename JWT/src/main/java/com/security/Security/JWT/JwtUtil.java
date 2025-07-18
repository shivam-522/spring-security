package com.security.Security.JWT;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

//JwtUtil ek utility class hai jo JWT (JSON Web Token) banane, usme se username nikaalne, aur us token ko validate karne ka kaam karti hai.
@Component //Now spring will amnage it as a bean. We can inject it anywhere using @Autowired annotation
public class JwtUtil {
	
    private final String SECRET_KEY = "shivam-secret-key-which-is-atleast-32char";// JWT token ko sign karne ke liye secret key chahiye hoti hai.Ye HMAC SHA256 algorithm ke liye at least 32-character long honi chahiye. Ye secret key dono – token banate waqt aur validate karte waqt use hoti hai.
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 hours This token will expire After 10 hours

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)//Token ka main payload — user kaun hai
                .setIssuedAt(new Date())// Token kab bana
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))// Expiry
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)// Sign with algo + secret key
                .compact(); // Final token string return
    }
    //JWT has 3 parts: Header + Payload + Signature.
    //Header: {"alg": "HS256", "typ": "JWT"}
    //Payload: {"sub": "shivam", "initial time": time, "exp": time}
    //Signature: HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), secret)

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());// Siging our secreate key
    }
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token, String username) {
        return username.equals(extractUsername(token)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
}
