package com.BankEngine.infrastructures.redis;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisLockService
{
  private final RedisTemplate<String,Object> redisTemplate;

  /**
   * @param key - LOCK:ACCOUNT:{id}
   * @param ttl lock timeout
   */
  // 🔐 1) Lock almaya çalış (success = true, fail = false)
  public String tryLock(String key, Duration ttl)
  {
    String lockValue = UUID.randomUUID().toString();

    Boolean success = redisTemplate.opsForValue().setIfAbsent(key, lockValue, ttl);

    if (Boolean.TRUE.equals(success))
    {
      log.debug("LOCK ACQUIRED → key={} value={}", key, lockValue);
      return lockValue;
    }

    return null; // lock alınamadı
  }


  /**
   * Redis lock ancak aynı request'e ait ise bırakılır.
   */

  // 🔐 2) Lock'u bırak
  public void unlock(String key, String lockValue)
  {
    try {
      String currentValue = (String) redisTemplate.opsForValue().get(key);

      // ❗ Only the owner can release the lock
      if (lockValue.equals(currentValue))
      {
        redisTemplate.delete(key);
        log.debug("LOCK RELEASED → key={}", key);
      }
      else
      {
        log.warn("LOCK NOT RELEASED → key={} value mismatch!", key);
      }
    }
    catch (Exception e)
    {
      log.error("UNLOCK ERROR → key={}", key, e);
    }
  }

  // 🔥 3) Retry'li lock (TransferService bunu kullanacak)
  public String lockOrThrow(String key)
  {
    Duration wait = Duration.ofMillis(100);
    Duration ttl = Duration.ofSeconds(5);

    for (int i = 0; i < 5; i++)
    {
      String lockValue = tryLock(key, ttl);

      if (lockValue != null)
        return lockValue;

      try
      {
        Thread.sleep(wait.toMillis());
      }
      catch (InterruptedException ignored) { }
    }

    throw new RuntimeException("Could not acquire lock for key=" + key);
  }


  /**
   * Convenience method – otomatik requestId üretmek istersen kullanılır.
   */
  public String generateRequestId() {
    return UUID.randomUUID().toString();
  }

}

/**
 *
 * Distributed Lock ne işe yarıyor?
 *
 * Aynı account üzerinde aynı anda iki işlem yapılmasın
 *
 * Double spending engellensin
 *
 * Concurrency patlaması olduğunda sistem deterministik kalsın
 *
 * DB seviyesinde optimistic/pessimistic lock’a yük binmesin
 *
 * Senin BankEngine projesi high concurrency'yi KALDIRABİLSİN
 *
 * Bu lock sadece “tek bir işlem” için değil →
 * Tüm transfer flow’unda tutarlılık için gereklidir.
 *
 */