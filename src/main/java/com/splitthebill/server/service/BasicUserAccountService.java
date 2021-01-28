package com.splitthebill.server.service;

import com.splitthebill.server.dto.BasicUserAccountCreateDto;
import com.splitthebill.server.model.user.BasicUserAccount;
import com.splitthebill.server.model.user.UserAccount;
import com.splitthebill.server.repository.BasicUserAccountRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
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

    public BasicUserAccount createUserAccount(BasicUserAccountCreateDto accountDto)
            throws DataIntegrityViolationException {
        //TODO implement rest
        BasicUserAccount userAccount = new BasicUserAccount(accountDto);
        if(basicUserAccountRepository.existsByUsernameOrEmail(accountDto.username, accountDto.email))
            throw new DataIntegrityViolationException("Username or email already exists.");
        return basicUserAccountRepository.save(userAccount);
    }

}
