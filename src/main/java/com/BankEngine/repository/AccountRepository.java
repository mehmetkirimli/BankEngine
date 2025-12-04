package com.BankEngine.repository;

import com.BankEngine.entity.Account;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,Long>
{
  Optional<Account> findByIban(String iban);

}
