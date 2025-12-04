package com.BankEngine.entity;

import com.BankEngine.enumaration.OutboxStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "outboxEvents")
@Getter
@Setter
public class OutboxEvent extends BaseEntity
{
  private Long aggegateId;
  private String eventType;
  @Column(columnDefinition = "TEXT")
  private String payload;

  @Enumerated(EnumType.STRING)
  private OutboxStatus status;

}
