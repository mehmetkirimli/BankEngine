package com.BankEngine.infrastructures.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisCacheService
{
  private final StringRedisTemplate redisTemplate;
  private final ObjectMapper objectMapper = new ObjectMapper();


  public <T> void set(String key,T value , Duration ttl)
  {
    try
    {
      String json = objectMapper.writeValueAsString(value);
      redisTemplate.opsForValue().set(key,json,ttl);
    }
    catch (JsonProcessingException e)
    {
      throw new RuntimeException("Cache set error : " + e.getMessage());
    }

  }

  public <T> T get(String key, Class<T> clazz) {
    try {
      String json = redisTemplate.opsForValue().get(key);
      if (json == null) return null;
      return objectMapper.readValue(json, clazz);
    } catch (Exception e) {
      return null;
    }
  }

  public void delete(String key) {
    redisTemplate.delete(key);
  }

}
