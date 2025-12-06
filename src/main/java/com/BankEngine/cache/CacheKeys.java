package com.BankEngine.cache;

public class CacheKeys {

  public static String accountDetail(Long id)
  {
    return "ACCOUNT:DETAIL:" + id;  // Tek hesap detayı için cache key
  }

  public static String accountList(Long clientId)
  {
    return "ACCOUNT:LIST:" + clientId; // Client'in account listesi için cache key
  }

  public static String transfer(Long id)
  {
    return "TRANSFER:" + id; // Transfer işlemi için cache key
  }
}
