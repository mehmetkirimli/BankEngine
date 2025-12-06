package com.BankEngine.cache;

import com.BankEngine.dto.AccountDto;
import com.BankEngine.entity.Account;
import com.BankEngine.mapper.AccountMapper;
import com.BankEngine.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountCacheService
{
  private final RedisTemplate<String,Object> redisTemplate;
  private final AccountRepository accountRepository;
  private final AccountMapper accountMapper;
  private final ObjectMapper objectMapper ;

  private final Duration TTL = Duration.ofMinutes(5);

  // 1) Client hesap listesini cache'den getir
  public List<AccountDto> getAccountsByClientId(Long clientId)
  {
    String key = CacheKeys.accountList(clientId);

    List<AccountDto> cached = (List<AccountDto>) redisTemplate.opsForValue().get(key);
    if (cached != null)
      return cached;

    // Cache yok → DB'den al
    List<Account> accounts = accountRepository.findByClientId(clientId);

    List<AccountDto> dtoList = accounts.stream()
        .map(accountMapper::toDto)
        .collect(Collectors.toList());

    // DTO'yu cache' yazıyoruz (entityi değil!)
    redisTemplate.opsForValue().set(key, dtoList, TTL);

    return dtoList;
  }
  // 2) Hesap detayını cache'den getir
  public AccountDto getAccountDetail(Long accountId)
  {
    String key = CacheKeys.accountDetail(accountId);

    Object cached = redisTemplate.opsForValue().get(key);

    if (cached != null)
    {
      try
      {
        //Json string ise olduğu gibi deserialize etcez
        String json = objectMapper.writeValueAsString(cached);
        return objectMapper.readValue(json, AccountDto.class);
      }
      catch (Exception e)
      {
        throw new RuntimeException("Cache deserialize error brother !" , e);
      }
    }

    Account account = accountRepository.findById(accountId)
        .orElseThrow(() -> new RuntimeException("Account not found"));

    AccountDto dto = accountMapper.toDto(account);

    redisTemplate.opsForValue().set(key, dto, TTL);
    return dto;
  }
  // 3) Cache invalidation
  public void evictAccountDetail(Long accountId)
  {
    redisTemplate.delete(CacheKeys.accountDetail(accountId));
  }
  public void evictClientAccountList(Long clientId)
  {
    redisTemplate.delete(CacheKeys.accountList(clientId));
  }
  public void setAccountDetail(Long id, AccountDto dto)
  {
    String key = CacheKeys.accountDetail(id);
    redisTemplate.opsForValue().set(key, dto, TTL);
  }
  public void setAccountList(Long clientId, List<AccountDto> dtoList)
  {
    String key = CacheKeys.accountList(clientId);
    redisTemplate.opsForValue().set(key, dtoList, TTL);
  }
  public List<AccountDto> getAccountList(Long clientId)
  {
    String key = CacheKeys.accountList(clientId);

    // 1) Önce cache’e bak
    List<AccountDto> cached = (List<AccountDto>) redisTemplate.opsForValue().get(key);
    if (cached != null)
    {
      return cached;
    }

    // 2) Cache boş => DB’den al
    List<AccountDto> dtoList = accountRepository
        .findByClientId(clientId)
        .stream()
        .map(accountMapper::toDto)
        .toList();

    // 3) Cache’e yaz
    setAccountList(clientId, dtoList);

    return dtoList;
  }
}

/*
Adım 1 — Client'ın hesap listesini cache’den oku
Redis’te CLIENT:ACCOUNTS:{clientId} var mı?
Varsa direkt dön → DB'ye hiç gitme.
Yoksa → DB’den oku → Cache'e yaz → return.

Adım 2 — Hesap detayını cache’den oku
Key: ACCOUNT:DETAIL:{accountId}
Aynı mantık → önce cache → yoksa DB → cache’e ekle → return.

Adım 3 — Cache invalidation

Yeni hesap oluşturulunca:
CLIENT:ACCOUNTS:{clientId} silinecek
çünkü liste değişti.

Transfer olunca:
Hem source hem target account detail cache silinir
Belki liste de değişebilir (bankacılıkta değişmez ama case study izin veriyor)
 */
