package com.BankEngine.dto;

import com.BankEngine.enumaration.AccountStatus;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class AccountDto {
  private Long id;
  private Long clientId;
  private String iban;
  private BigDecimal balance;
  private String currency;
  private AccountStatus status;
}
