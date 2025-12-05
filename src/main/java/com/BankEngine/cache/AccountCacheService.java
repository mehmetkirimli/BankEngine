package com.BankEngine.cache;

import com.BankEngine.dto.AccountDto;
import com.BankEngine.entity.Account;
import com.BankEngine.mapper.AccountMapper;
import com.BankEngine.repository.AccountRepository;
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

  private final Duration TTL = Duration.ofMinutes(5);

  private String clientAccountsKey(Long clientId) {
    return "CLIENT:ACCOUNTS:" + clientId;
  }

  private String accountDetailKey(Long accountId) {
    return "ACCOUNT:DETAIL:" + accountId;
  }

  // 1) Client hesap listesini cache'den getir
  public List<AccountDto> getAccountsByClientId(Long clientId)
  {
    String key = clientAccountsKey(clientId);

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
    String key = accountDetailKey(accountId);

    AccountDto cached = (AccountDto) redisTemplate.opsForValue().get(key);
    if (cached != null)
      return cached;

    Account account = accountRepository.findById(accountId)
        .orElseThrow(() -> new RuntimeException("Account not found"));

    AccountDto dto = accountMapper.toDto(account);

    redisTemplate.opsForValue().set(key, dto, TTL);
    return dto;
  }

  // 3) Cache invalidation
  public void evictClientAccounts(Long clientId) {
    redisTemplate.delete(clientAccountsKey(clientId));
  }

  public void evictAccountDetail(Long accountId) {
    redisTemplate.delete(accountDetailKey(accountId));
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
