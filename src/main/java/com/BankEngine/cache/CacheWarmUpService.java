package com.BankEngine.cache;

import com.BankEngine.dto.AccountDto;
import com.BankEngine.entity.Account;
import com.BankEngine.mapper.AccountMapper;
import com.BankEngine.repository.AccountRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheWarmUpService
{
  private final AccountRepository  accountRepository;
  private final AccountCacheService accountCacheService;

  @EventListener(ApplicationReadyEvent.class)
  public void warmUpCache()
  {
    log.info("🚀 Cache Warm-Up started...");

    List<Account> allAccounts = accountRepository.findAll();


    for(Account acc : allAccounts)
    {
      accountCacheService.setAccountDetail(acc.getId(),accountCacheService.toDto(acc)); // Tüm Accountları cacheleyen kısım
    }

    Map<Long,List<AccountDto>> grouped = allAccounts.stream()
            .map(accountCacheService::toDto)
                .collect(Collectors.groupingBy(AccountDto::getClientId));

    grouped.forEach((clientId,list) -> {
      accountCacheService.setAccountList(clientId,list);
    });

    log.info("🔥 Cache Warm-Up finished. {} hesap cache’e yüklendi.", allAccounts.size());
  }

}
