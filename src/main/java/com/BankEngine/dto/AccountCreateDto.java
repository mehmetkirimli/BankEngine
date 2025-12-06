package com.BankEngine.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountCreateDto {
  private Long clientId;
  private String iban;
  private BigDecimal balance;
  private String currency;
}

