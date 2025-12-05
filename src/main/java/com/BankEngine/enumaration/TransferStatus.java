package com.BankEngine.enumaration;

import lombok.Getter;

@Getter
  public enum TransferStatus {
    PENDING(1,"PENDING"),
    COMPLETED(2,"COMPLETED"),
    FAILED(3,"FAILED");

    private Integer kod;
    private String message;

    TransferStatus(Integer kod,String message)
    {
      this.kod=kod;
      this.message = message;
    }
  }

