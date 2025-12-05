package com.BankEngine.enumaration;

import lombok.Getter;

@Getter
public enum OutboxStatus {
  NEW(1,"NEW"),
  SENT(2,"SENT");
  private Integer kod;
  private String message;

  OutboxStatus(Integer kod,String message)
  {
    this.kod=kod;
    this.message = message;
  }
}
