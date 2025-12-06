package com.BankEngine.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferCreateDto
{
  private Long targetAccountId;
  private Long sourceAccountId;
  private BigDecimal amount;
}
