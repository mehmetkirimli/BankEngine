package com.BankEngine.entity;

import com.BankEngine.enumaration.OutboxStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class OutboxEvent extends BaseEntity
{
  private Long aggegateId;
  private String eventType;
  @Column(columnDefinition = "TEXT")
  private String payload;

  @Enumerated(EnumType.STRING)
  private OutboxStatus status;

}
