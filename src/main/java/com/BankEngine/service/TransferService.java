package com.BankEngine.service;

import com.BankEngine.cache.AccountCacheService;
import com.BankEngine.dto.TransferCreateDto;
import com.BankEngine.dto.TransferDto;
import com.BankEngine.entity.Account;
import com.BankEngine.entity.OutboxEvent;
import com.BankEngine.entity.Transfer;
import com.BankEngine.enumaration.TransferStatus;
import com.BankEngine.exception.BusinessException;
import com.BankEngine.exception.NotFoundException;
import com.BankEngine.infrastructures.redis.RedisLockService;
import com.BankEngine.mapper.TransferMapper;
import com.BankEngine.repository.AccountRepository;
import com.BankEngine.repository.OutboxRepository;
import com.BankEngine.repository.TransferRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {

  private final TransferRepository transferRepository;
  private final AccountRepository accountRepository;
  private final OutboxRepository outboxRepository;

  private final AccountCacheService accountCacheService;
  private final RedisLockService redisLockService;

  private final TransferMapper mapper;

  @Transactional
  public TransferDto create(TransferCreateDto dto) {

    validateTransferRequest(dto);

    Account source = accountRepository.findById(dto.getSourceAccountId())
        .orElseThrow(() -> new NotFoundException("Source account not found"));

    Account target = accountRepository.findById(dto.getTargetAccountId())
        .orElseThrow(() -> new NotFoundException("Target account not found"));

    if (source.getBalance().compareTo(dto.getAmount()) < 0) {
      throw new BusinessException("Insufficient balance");
    }

    // Şimdilik sadece kayıt altına alıyoruz
    Transfer transfer = mapper.toEntity(dto);
    transfer.setStatus(TransferStatus.PENDING);

    transferRepository.save(transfer);

    log.info("Transfer requested. id={}, from={}, to={}, amount={}",
        transfer.getId(), source.getId(), target.getId(), dto.getAmount());

    return mapper.toDto(transfer);
  }
  @Transactional(readOnly = true)
  public TransferDto get(Long id) {
    Transfer t = transferRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Transfer not found"));
    return mapper.toDto(t);
  }
  private void validateTransferRequest(TransferCreateDto dto) {
    if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new BusinessException("Transfer amount must be positive");
    }

    if (dto.getSourceAccountId().equals(dto.getTargetAccountId())) {
      throw new BusinessException("Source and target accounts cannot be same");
    }
  }

  @Transactional
  public TransferDto transfer (TransferCreateDto dto)
  {
    Long sourceId = dto.getSourceAccountId();
    Long targetId = dto.getTargetAccountId();

    if(sourceId.equals(targetId))
    {
      throw new BusinessException(("Source and target account cannot be same !"));
    }

    //1-) Deadlock prevent -> Sort

    Long first = Math.min(sourceId,targetId); // Bu olmazsa deadlock olur , 2 thread karşılıklı birbirini kitler
    Long second = Math.max(sourceId,targetId);

    String firstKey = "LOCK:ACCOUNT:"+first;
    String secondKey = "LOCK:ACCOUNT"+second;

    String firstLock = null;
    String secondLock = null;

    try
    {
      //2-) Lock Alınacak

      firstLock= redisLockService.lockOrThrow(firstKey); // Lock alınmazsa transfer başlamaz , sistem tutarlı kalır
      secondLock = redisLockService.lockOrThrow(secondKey);

      //3-) Db İşlemleri (Atomic) Spring @Transactional ile:  1-balance düşme  2-balance artırma      3-transfer kaydetme      => Hepsi tek transaction’da.
      Account source = accountRepository.findById(sourceId).orElseThrow(() -> new BusinessException("Source account not foundd !!"));

      Account target = accountRepository.findById(targetId).orElseThrow(() -> new BusinessException("Target account not found"));

      if (source.getBalance().compareTo(dto.getAmount()) < 0)
      {
        throw new BusinessException("Insufficient balance");
      }

      // Güncelle

      source.setBalance(source.getBalance().subtract(dto.getAmount()));
      target.setBalance(target.getBalance().add(dto.getAmount()));

      accountRepository.save(source);
      accountRepository.save(target);

      //Transfer Kaydı

      Transfer transfer = new Transfer();

      transfer.setSourceAccountId(sourceId);
      transfer.setTargetAccountId(targetId);

      transfer.setAmount(dto.getAmount());
      transfer.setStatus(TransferStatus.COMPLETED);

      transferRepository.save(transfer);



      // 4-) Outbox Event
      OutboxEvent event = new OutboxEvent();
      event.setAggegateId(transfer.getId());
      event.setEventType("TRANSFER COMPLETED");
      event.setPayload("{\"transferId\":" + transfer.getId() + "}");
      outboxRepository.save(event);



      // 5-)  Cache Invalidation

      accountCacheService.evictAccountDetail(sourceId);
      accountCacheService.evictAccountDetail(targetId);

      return mapper.toDto(transfer);



    }
    finally
    {
      //6-) Lockları serbest bırak
      if (firstLock != null) redisLockService.unlock(firstKey,firstLock);
      if (secondLock != null) redisLockService.unlock(secondKey,secondLock);
    }


  }
}
