package com.BankEngine.service;

import com.BankEngine.dto.TransferCreateDto;
import com.BankEngine.dto.TransferDto;
import com.BankEngine.entity.Account;
import com.BankEngine.entity.Transfer;
import com.BankEngine.enumaration.TransferStatus;
import com.BankEngine.exception.BusinessException;
import com.BankEngine.exception.NotFoundException;
import com.BankEngine.mapper.TransferMapper;
import com.BankEngine.repository.AccountRepository;
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
}
