package com.BankEngine.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class TransferCreateDto
{
  private Long targetAccountId;
  private Long sourceAccountId;
  private BigDecimal amount;
}
