package com.BankEngine.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class BaseEntity
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private LocalDateTime createdAt;
  private  boolean isDeleted = false;
  // Kayıt oluşturulmadan hemen önce zaman damgasını otomatik ayarlaması için
  @PrePersist
  protected void onCreate()
  {
    this.createdAt = LocalDateTime.now();
  }
}

/*
SELECT Sorguları: Artık tüm sorgularda WHERE isDeleted = false koşulunu eklemeyi unutmaaa
 */