package com.splitthebill.server.controller;

import com.splitthebill.server.dto.JwtResponseDto;
import com.splitthebill.server.dto.LoginRequestDto;
import com.splitthebill.server.dto.BasicUserAccountCreateDto;
import com.splitthebill.server.dto.ThirdPartyUserAccountCreateDto;
import com.splitthebill.server.model.user.BasicUserAccount;
import com.splitthebill.server.model.user.ThirdPartyUserAccount;
import com.splitthebill.server.model.user.UserAccount;
import com.splitthebill.server.security.JwtUtils;
import com.splitthebill.server.service.BasicUserAccountService;
import com.splitthebill.server.service.ThirdPartyUserAccountService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @NonNull
    AuthenticationManager authenticationManager;

    @NonNull
    BasicUserAccountService basicUserAccountService;

    @NonNull
    ThirdPartyUserAccountService thirdPartyUserAccountService;

    @NonNull
    PasswordEncoder encoder;

    @NonNull
    JwtUtils jwtUtils;

    // This is temp endpoint to make testing easier, will be removed when connecting with client app
    @GetMapping("/tempThirdPartyEndpoint/{token}")
    public ResponseEntity<?> getThirdPartyToken(@PathVariable String token){
        return ResponseEntity.ok(token);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequest) {

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        }catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body("Incorrect username or password.");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtTokenForBasicAccount(authentication);

        return ResponseEntity.ok(new JwtResponseDto(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody BasicUserAccountCreateDto userAccount) {
        String unHashedPassword = userAccount.password;
        userAccount.password = encoder.encode(userAccount.password);
        try{
            basicUserAccountService.createUserAccount(userAccount);
        }catch (DataIntegrityViolationException exception){
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
        return authenticateUser(new LoginRequestDto(userAccount.username, unHashedPassword));
    }

    @PostMapping("/oauth2/signin")
    public ResponseEntity<?> registerThirdPartyUser(@Valid @RequestBody ThirdPartyUserAccountCreateDto userAccount) {
        ThirdPartyUserAccount account;
        try {
            account = thirdPartyUserAccountService.getUserAccountByEmail(userAccount.email);
        } catch (EntityNotFoundException e) {
            account = thirdPartyUserAccountService.createUserAccount(userAccount);
        }
        String jwt = jwtUtils.generateJwtToken(account.getEmail());
        return ResponseEntity.ok(new JwtResponseDto(jwt));
    }
}
