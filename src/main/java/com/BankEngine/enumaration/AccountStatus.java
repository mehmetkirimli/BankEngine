package com.BankEngine.enumaration;

import lombok.Getter;

@Getter
public enum AccountStatus {
  ACTIVE(1,"ACTIVE"),
  PASSIVE(2,"PASSIVE"),
  FROZEN(3,"FROZEN"),
  BLOCKED(4,"BLOCKED");


  private Integer kod;
  private String message;

  AccountStatus(Integer kod,String message)
  {
    this.kod=kod;
    this.message = message;
  }
}