package com.splitthebill.server.service;

import com.splitthebill.server.dto.BasicUserAccountCreateDto;
import com.splitthebill.server.model.user.BasicUserAccount;
import com.splitthebill.server.model.user.UserAccount;
import com.splitthebill.server.repository.BasicUserAccountRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class BasicUserAccountService {

    @NonNull
    BasicUserAccountRepository basicUserAccountRepository;


    public BasicUserAccount getUserAccountById(Long id) throws Exception {
        return basicUserAccountRepository.findById(id).orElseThrow(Exception::new);
    }

    public BasicUserAccount getUserAccountByUsername(String username) throws EntityNotFoundException {
        return basicUserAccountRepository.findByUsername(username).orElseThrow(EntityNotFoundException::new);
    }

    public BasicUserAccount createUserAccount(BasicUserAccountCreateDto accountDto) throws Exception {
        //TODO implement rest
        BasicUserAccount userAccount = new BasicUserAccount(accountDto);
        return basicUserAccountRepository.save(userAccount);
    }

}
