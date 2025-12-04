package com.BankEngine.mapper;

import com.BankEngine.dto.AccountCreateDto;
import com.BankEngine.dto.AccountDto;
import com.BankEngine.entity.Account;
import com.BankEngine.enumaration.AccountStatus;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

  public Account toEntity(AccountCreateDto dto) {
    Account acc = new Account();
    acc.setClientId(dto.getClientId());
    acc.setIban(dto.getIban());
    acc.setBalance(dto.getBalance());
    acc.setCurrency(dto.getCurrency());
    acc.setStatus(AccountStatus.ACTIVE);
    return acc;
  }

  public AccountDto toDto(Account acc) {
    AccountDto dto = new AccountDto();
    dto.setId(acc.getId());
    dto.setClientId(acc.getClientId());
    dto.setIban(acc.getIban());
    dto.setBalance(acc.getBalance());
    dto.setCurrency(acc.getCurrency());
    dto.setStatus(acc.getStatus());
    return dto;
  }
}
