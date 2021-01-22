package com.splitthebill.server.service;

import com.splitthebill.server.model.user.UserAccount;
import com.splitthebill.server.repository.UserAccountRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;

@Service
@RequiredArgsConstructor
public class UserAccountService {

    @NonNull
    private UserAccountRepository userAccountRepository;

    @NonNull
    private BasicUserAccountService basicUserAccountService;

    public UserAccount getUserAccountByIdentifierAttribute(String attribute){
        //Identifier attribute is considered email or username
        UserAccount userAccount;
        try {
            userAccount = userAccountRepository.findByEmail(attribute).orElseThrow(EntityExistsException::new);
        }catch(EntityExistsException e){
            userAccount = basicUserAccountService.getUserAccountByUsername(attribute);
        }
        return userAccount;
    }
}
