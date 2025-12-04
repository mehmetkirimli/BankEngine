package com.BankEngine.exception;

import com.fasterxml.jackson.databind.ser.Serializers.Base;

public class BaseException  extends  RuntimeException
{
  public BaseException(String message){
    super(message);
  }

}
