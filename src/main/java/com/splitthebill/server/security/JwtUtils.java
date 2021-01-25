package com.splitthebill.server.security;

import com.splitthebill.server.model.user.Person;
import com.splitthebill.server.model.user.UserAccount;
import io.jsonwebtoken.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    @Value("${splitthebill.app.jwtSecret}")
    private String jwtSecret;
    @Value("${splitthebill.app.jwtExpirationMs}")
    private int jwtExpirationMs;
    @NonNull
    private UserDetailsServiceImpl userDetailsService;

    public String generateJwtTokenForBasicAccount(Authentication authentication) {
        UserDetailsImpl userAccount = (UserDetailsImpl) authentication.getPrincipal();

        return generateJwtToken(userAccount.getUsername());
    }

    public String generateJwtToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getTokenSubject(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public UserAccount getUserAccountFromAuthentication(Authentication authentication) {
        UserDetailsImpl userAccount = (UserDetailsImpl) authentication.getPrincipal();
        return userDetailsService.loadUserAccountByUsername(userAccount.getUsername());
    }

    public Person getPersonFromAuthentication(Authentication authentication) {
        Person person = getUserAccountFromAuthentication(authentication).getPerson();
        if(person == null)
            throw new EntityNotFoundException("User account has no person assigned.");
        return person;
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException ignored) {

        }
        return false;
    }
}
