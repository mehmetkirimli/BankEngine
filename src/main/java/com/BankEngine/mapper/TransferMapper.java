package com.BankEngine.mapper;

import com.BankEngine.dto.TransferCreateDto;
import com.BankEngine.dto.TransferDto;
import com.BankEngine.entity.Transfer;
import com.BankEngine.enumaration.TransferStatus;
import org.springframework.stereotype.Component;

@Component
public class TransferMapper {

  public Transfer toEntity(TransferCreateDto dto) {
    Transfer t = new Transfer();
    t.setTargetAccountId(dto.getTargetAccountId());
    t.setSourceAccountId(dto.getSourceAccountId());
    t.setAmount(dto.getAmount());
    t.setStatus(TransferStatus.PENDING);
    return t;
  }

  public TransferDto toDto(Transfer t) {
    TransferDto dto = new TransferDto();
    dto.setId(t.getId());
    dto.setSourceAccountId(t.getSourceAccountId());
    dto.setTargetAccountId(t.getTargetAccountId());
    dto.setAmount(t.getAmount());
    dto.setStatus(t.getStatus());
    return dto;
  }
}

