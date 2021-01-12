package com.splitthebill.server.service;

import com.splitthebill.server.dto.UserAccountCreateDto;
import com.splitthebill.server.model.user.UserAccount;
import com.splitthebill.server.repository.UserAccountRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAccountService {

    @NonNull
    UserAccountRepository userAccountRepository;

    public UserAccount getUserAccountById(Long id) throws Exception {
        return userAccountRepository.findById(id).orElseThrow(Exception::new);
    }

    public UserAccount createUserAccount(UserAccountCreateDto accountDto) throws Exception {
        //TODO implement rest
        return null;
    }
}
