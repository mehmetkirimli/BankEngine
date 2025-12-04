package com.BankEngine.entity;

import com.BankEngine.enumaration.AccountStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account extends BaseEntity
{
  private Long clientId ;

  @Column(unique = true)
  private String iban;

  private BigDecimal balance;

  private String currency;

  @Enumerated(EnumType.STRING)
  private AccountStatus status;
}
