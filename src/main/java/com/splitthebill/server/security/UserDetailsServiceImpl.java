package com.splitthebill.server.security;

import com.splitthebill.server.model.user.BasicUserAccount;
import com.splitthebill.server.model.user.UserAccount;
import com.splitthebill.server.service.BasicUserAccountService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    @NonNull
    BasicUserAccountService basicUserAccountService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        BasicUserAccount userAccount = basicUserAccountService.getUserAccountByUsername(username);

        return UserDetailsImpl.build(userAccount);
    }
}

