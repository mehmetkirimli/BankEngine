package com.BankEngine.controller;


import com.BankEngine.dto.TransferCreateDto;
import com.BankEngine.dto.TransferDto;
import com.BankEngine.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

  private final TransferService transferService;

  @PostMapping
  public TransferDto create(@RequestBody TransferCreateDto dto) {
    return transferService.create(dto);
  }

  @GetMapping("/{id}")
  public TransferDto get(@PathVariable Long id) {
    return transferService.get(id);
  }
}
