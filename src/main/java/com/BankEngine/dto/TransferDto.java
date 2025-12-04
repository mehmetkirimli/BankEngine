package com.BankEngine.dto;

import com.BankEngine.enumaration.TransferStatus;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class TransferDto
{
  private Long id;
  private Long targetAccountId;
  private Long sourceAccountId;
  private BigDecimal amount;
  private TransferStatus status;

}
