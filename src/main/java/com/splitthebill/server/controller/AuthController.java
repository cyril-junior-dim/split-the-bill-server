package com.splitthebill.server.controller;

import com.splitthebill.server.dto.JwtResponseDto;
import com.splitthebill.server.dto.LoginRequestDto;
import com.splitthebill.server.dto.BasicUserAccountCreateDto;
import com.splitthebill.server.security.JwtUtils;
import com.splitthebill.server.security.UserDetailsImpl;
import com.splitthebill.server.service.BasicUserAccountService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

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
    PasswordEncoder encoder;

    @NonNull
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtTokenForBasicAccount(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponseDto(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody BasicUserAccountCreateDto userAccount) {
        userAccount.password = encoder.encode(userAccount.password);
        try{
            basicUserAccountService.createUserAccount(userAccount);
        }catch (Exception ignored){}

        return ResponseEntity.ok().build();
    }
}
