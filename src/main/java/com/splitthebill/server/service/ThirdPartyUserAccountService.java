package com.splitthebill.server.service;

import com.splitthebill.server.dto.ThirdPartyUserAccountCreateDto;
import com.splitthebill.server.model.user.ThirdPartyUserAccount;
import com.splitthebill.server.repository.ThirdPartyUserAccountRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class ThirdPartyUserAccountService {

    @NonNull
    ThirdPartyUserAccountRepository thirdPartyUserAccountRepository;


    public ThirdPartyUserAccount getUserAccountById(Long id) throws Exception {
        return thirdPartyUserAccountRepository.findById(id).orElseThrow(Exception::new);
    }

    public ThirdPartyUserAccount getUserAccountByEmail(String email) {
        return thirdPartyUserAccountRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
    }

    public ThirdPartyUserAccount createUserAccount(ThirdPartyUserAccountCreateDto accountDto) throws EntityExistsException {
        ThirdPartyUserAccount userAccount = new ThirdPartyUserAccount(accountDto);
        if(thirdPartyUserAccountRepository.existsByEmail(accountDto.email))
            throw new EntityExistsException("This email address `" + accountDto.email + "`has been already used.");
        return thirdPartyUserAccountRepository.save(userAccount);
    }

}
