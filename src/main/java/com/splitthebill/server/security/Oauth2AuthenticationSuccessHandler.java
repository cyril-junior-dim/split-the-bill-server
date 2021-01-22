package com.splitthebill.server.security;

import com.splitthebill.server.dto.ThirdPartyUserAccountCreateDto;
import com.splitthebill.server.model.user.ThirdPartyUserAccount;
import com.splitthebill.server.service.ThirdPartyUserAccountService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Configuration("oauth2authSuccessHandler")
@RequiredArgsConstructor
public class Oauth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @NonNull
    private ThirdPartyUserAccountService thirdPartyUserAccountService;

    @NonNull
    private RedirectStrategy redirectStrategy;

    @NonNull
    private JwtUtils jwtUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        Map<String, Object> attributes = token.getPrincipal().getAttributes();
        ThirdPartyUserAccountCreateDto userAccountCreateDto = new ThirdPartyUserAccountCreateDto();
        if(token.getAuthorizedClientRegistrationId().equals("google")){
            userAccountCreateDto.provider = "google";
        }else if(token.getAuthorizedClientRegistrationId().equals("facebook")){
            userAccountCreateDto.provider = "facebook";
        }
        userAccountCreateDto.email = String.valueOf(attributes.get("email"));
        userAccountCreateDto.name = String.valueOf(attributes.get("name"));
        ThirdPartyUserAccount account;
        try {
            account = thirdPartyUserAccountService.getUserAccountByEmail(userAccountCreateDto.email);
        } catch (EntityNotFoundException e) {
            account = thirdPartyUserAccountService.createUserAccount(userAccountCreateDto);
        }

        String userToken = jwtUtils.generateJwtToken(account.getEmail());

        // TODO redirect to client application using deep link
        this.redirectStrategy
                .sendRedirect(httpServletRequest, httpServletResponse, "/auth/tempThirdPartyEndpoint/"+userToken);
    }
}
