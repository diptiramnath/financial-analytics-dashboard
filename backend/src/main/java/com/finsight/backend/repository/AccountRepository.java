package com.finsight.backend.repository;

import com.finsight.backend.entity.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends MongoRepository<Account,String> {
    List<Account> findByUserId(String id);
    Optional<Account> findByIdAndUserId(String id,String userId);
}
