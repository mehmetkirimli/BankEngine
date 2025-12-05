package com.BankEngine.repository;

import com.BankEngine.entity.Account;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long>
{
  Optional<Account> findByIban(String iban);

  List<Account> findByClientId(Long clientId);
}
