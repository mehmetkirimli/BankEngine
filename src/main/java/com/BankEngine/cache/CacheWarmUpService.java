package com.BankEngine.cache;

import com.BankEngine.dto.AccountDto;
import com.BankEngine.entity.Account;
import com.BankEngine.mapper.AccountMapper;
import com.BankEngine.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheWarmUpService {

  private final AccountRepository accountRepository;
  private final AccountCacheService accountCacheService;
  private final AccountMapper accountMapper;

  @EventListener(ApplicationReadyEvent.class)
  public void warmUpCache()
  {

    log.info("🚀 Cache Warm-Up started...");

    // 1) Tüm hesapları DB’den 1 kere çek
    List<Account> allAccounts = accountRepository.findAll();
    log.info("DB’den {} hesap bulundu.", allAccounts.size());

    // 2) Account Detail doldur
    for (Account acc : allAccounts)
    {
      AccountDto dto = accountMapper.toDto(acc);
      accountCacheService.setAccountDetail(acc.getId(), dto);
    }

    // 3) Client bazında grupla
    Map<Long, List<AccountDto>> grouped = allAccounts.stream()
            .map(accountMapper::toDto)
            .collect(Collectors.groupingBy(AccountDto::getClientId));

    // 4) Client hesap listelerini cache’e yaz
    grouped.forEach((clientId, dtoList) -> {
      accountCacheService.setAccountList(clientId, dtoList);
    });

    log.info("🔥 Cache Warm-Up finished. {} hesap cache’e yüklendi.", allAccounts.size());
  }
}
