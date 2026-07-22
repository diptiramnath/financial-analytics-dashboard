package com.finsight.backend.service;

import com.finsight.backend.dto.AccountResponse;
import com.finsight.backend.dto.CreateAccountRequest;
import com.finsight.backend.dto.UpdateAccountRequest;
import com.finsight.backend.entity.Account;
import com.finsight.backend.entity.User;
import com.finsight.backend.exception.AccountNotFoundException;
import com.finsight.backend.repository.AccountRepository;
import org.springframework.data.repository.core.support.RepositoryMethodInvocationListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final CurrentUserService currentUserService;
    //private final RepositoryMethodInvocationListener repositoryMethodInvocationListener;

    public AccountService(AccountRepository accountRepository, CurrentUserService currentUserService) {
        this.accountRepository = accountRepository;
        this.currentUserService = currentUserService;
        //this.repositoryMethodInvocationListener = repositoryMethodInvocationListener;
    }

    public AccountResponse createAccount(CreateAccountRequest request){
        User currentUser=currentUserService.getCurrentUser();
        Account account=Account.builder()
                .userId(currentUser.getId())
                .name(request.getName())
                .type(request.getType())
                .balance(request.getBalance())
                .currency(request.getCurrency())
                .institution(request.getInstitution())
                .maskedAccountNumber(request.getMaskedAccountNumber())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Account savedAccount=accountRepository.save(account);
        return mapToResponse(savedAccount);
    }

    private AccountResponse mapToResponse(Account account){
        return AccountResponse.builder()
                .id(account.getId())
                .name(account.getName())
                .type(account.getType())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .institution(account.getInstitution())
                .maskedAccountNumber(account.getMaskedAccountNumber())
                .build();
    }

    public List<AccountResponse> getAccounts(){
        User currentUser=currentUserService.getCurrentUser();
        return accountRepository
                .findByUserId(currentUser.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public AccountResponse getAccount(String id){
        User currentUser=currentUserService.getCurrentUser();
        Account account=accountRepository
                .findByIdAndUserId(id,currentUser.getId())
                .orElseThrow(()->new AccountNotFoundException("Account not found"));
        return mapToResponse(account);
    }

    public void deleteAccount(String id){
        User currentUser=currentUserService.getCurrentUser();
        Account account=accountRepository
                .findByIdAndUserId(id,currentUser.getId())
                .orElseThrow(()->new AccountNotFoundException("Account not found"));
        accountRepository.delete(account);
    }

    public AccountResponse updateAccount(String id,UpdateAccountRequest request){
        User currentUser=currentUserService.getCurrentUser();
        Account account=accountRepository
                .findByIdAndUserId(id,currentUser.getId())
                .orElseThrow(() ->
                        new AccountNotFoundException("Account not found"));
        if(request.getName()!=null)
            account.setName(request.getName());
        if(request.getType()!=null)
            account.setType(request.getType());
        if(request.getBalance()!=null)
            account.setBalance(request.getBalance());
        if(request.getCurrency()!=null)
            account.setCurrency(request.getCurrency());
        if(request.getInstitution()!=null)
            account.setInstitution(request.getInstitution());
        if(request.getMaskedAccountNumber()!=null)
            account.setMaskedAccountNumber(request.getMaskedAccountNumber());
        account.setUpdatedAt(LocalDateTime.now());
        Account saved=accountRepository.save(account);
        return mapToResponse(saved);
    }
}
