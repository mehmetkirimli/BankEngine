package com.BankEngine.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class AccountCreateDto {
  private Long clientId;
  private String iban;
  private BigDecimal balance;
  private String currency;
}

