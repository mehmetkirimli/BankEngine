package com.BankEngine.infrastructures.outboxWorker;

import com.BankEngine.entity.OutboxEvent;
import com.BankEngine.enumaration.OutboxStatus;
import com.BankEngine.infrastructures.rabbitMq.EventPublisher;
import com.BankEngine.infrastructures.redis.RedisLockService;
import com.BankEngine.repository.OutboxRepository;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/***
 * ⏳ Bu worker 1 saniyede bir çalışacak
 * 🔐 Aynı anda iki instance çalışmasın diye global lock alacağız
 * 🚀 NEW kayıtları bulup publish edecek
 * ✔ başarılı olanları SENT yapacak
 * ♻ başarısız olanları retry bırakacak
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxProcessor
{
  private final OutboxRepository outboxRepository;
  private final EventPublisher publisher;
  private final RedisLockService redisLockService;

  private static final String LOCK_KEY = "LOCK:OUTBOX:PROCESSOR";

  @Scheduled(fixedDelay = 1000)
  public void process()
  {
    String lockId = redisLockService.tryLock(LOCK_KEY, Duration.ofSeconds(5));

    if(lockId == null)
    {
      //başka instance çalışıyorsa demeekkii
      return;
    }

    try {
      List<OutboxEvent> events = outboxRepository.findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus.NEW);

      if (events.isEmpty())
        return;

      log.info("OutboxProcessor found {} events", events.size());

      for (OutboxEvent event : events) {

        try {
          publisher.publish(event.getEventType(), event.getPayload());

          event.setStatus(OutboxStatus.SENT);
          outboxRepository.save(event);

          log.info("Outbox event SENT. id={}", event.getId());

        } catch (Exception ex) {
          log.error("Failed to process outbox event id={} - will retry later", event.getId() , ex);
        }
      }

    } finally {
      redisLockService.unlock(LOCK_KEY, lockId);
    }

  }

}
