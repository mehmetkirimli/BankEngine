package com.BankEngine.infrastructures.rabbitMq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisher
{
  private final RabbitTemplate rabbitTemplate;
  private static final String EXCHANGE = "bankengine.events";

  public void publish (String eventType , String payload)
  {
    try
    {
      String routingKey = "event." + eventType.toUpperCase();

      log.info("Publishing event. type={}, payload={}", eventType, payload);

      rabbitTemplate.convertAndSend(EXCHANGE,routingKey,payload);
    }
    catch (Exception e)
    {
      log.error("RabbitMq publis failed : " + e);
      throw e;
    }

  }

}
