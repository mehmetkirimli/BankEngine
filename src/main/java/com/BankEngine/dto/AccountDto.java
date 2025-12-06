package com.BankEngine.dto;

import com.BankEngine.enumaration.AccountStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
  private Long id;
  private Long clientId;
  private String iban;
  private BigDecimal balance;
  private String currency;
  private AccountStatus status;
  private LocalDateTime createdAt;
}
