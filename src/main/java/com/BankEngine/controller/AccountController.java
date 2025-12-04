package com.BankEngine.controller;

import com.BankEngine.dto.AccountCreateDto;
import com.BankEngine.dto.AccountDto;
import com.BankEngine.service.AccountService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService accountService;

  @PostMapping
  public AccountDto create(@RequestBody AccountCreateDto dto) {
    return accountService.create(dto);
  }

  @GetMapping("/{id}")
  public AccountDto get(@PathVariable Long id) {
    return accountService.get(id);
  }

  @GetMapping
  public List<AccountDto> getAll() {
    return accountService.getAll();
  }
}
