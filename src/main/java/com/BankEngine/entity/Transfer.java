package com.BankEngine.entity;

import com.BankEngine.enumaration.TransferStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "transfers")
public class Transfer extends BaseEntity
{
  private Long targetAccountId ;
  private Long sourceAccountId;
  private BigDecimal amount;
  @Enumerated(EnumType.STRING)
  private TransferStatus status;

}
