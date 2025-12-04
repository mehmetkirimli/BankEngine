package com.BankEngine.service;

import com.BankEngine.dto.AccountCreateDto;
import com.BankEngine.dto.AccountDto;
import com.BankEngine.entity.Account;
import com.BankEngine.exception.BusinessException;
import com.BankEngine.exception.NotFoundException;
import com.BankEngine.mapper.AccountMapper;
import com.BankEngine.repository.AccountRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

  private final AccountRepository accountRepository;
  private final AccountMapper mapper;

  @Transactional
  public AccountDto create(AccountCreateDto dto) {

    validateAccountCreateRequest(dto);

    if (accountRepository.findByIban(dto.getIban()).isPresent()) {
      throw new BusinessException("IBAN already exists");
    }

    Account entity = mapper.toEntity(dto);

    accountRepository.save(entity);

    log.info("Account created. id={}, iban={}", entity.getId(), entity.getIban());

    return mapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  public AccountDto get(Long id) {
    Account acc = accountRepository.findById(id).orElseThrow(() -> new NotFoundException("Account not found: " + id));

    return mapper.toDto(acc);
  }

  @Transactional(readOnly = true)
  public List<AccountDto> getAll()
  {
    return accountRepository.findAll()
        .stream()
        .map(mapper::toDto)
        .toList();
  }

  private void validateAccountCreateRequest(AccountCreateDto dto)
  {
    if (dto.getBalance().compareTo(BigDecimal.ZERO) < 0)
    {
      throw new BusinessException("Balance cannot be negative");
    }

    if (dto.getIban() == null || dto.getIban().isBlank())
    {
      throw new BusinessException("IBAN is required");
    }
  }
}
