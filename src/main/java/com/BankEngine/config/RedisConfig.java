package com.BankEngine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  /**
   * Varsayılan RedisTemplate'i, String anahtarlar ve JSON değerler kullanacak şekilde ayarlar.
   * Bu sayede, cache'e konulan AccountDto gibi objeler düzgünce serileştirilir.
   */
  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {

    // 1. Yeni bir RedisTemplate<String, Object> örneği oluşturulur.
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    // 2. Anahtarlar için String Serileştirici ayarlanır. (Örn: "account:123")
    // Redis'te anahtarların okunabilir olması için önemlidir.
    StringRedisSerializer stringSerializer = new StringRedisSerializer();
    template.setKeySerializer(stringSerializer);
    template.setHashKeySerializer(stringSerializer);

    // 3. Değerler için JSON Serileştirici ayarlanır. (Objeleri JSON'a çevirir)
    // Bu, AccountDto'nuzun Serializable olmasını gerektirmez ve daha okunabilir format sağlar.
    GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
    template.setValueSerializer(jsonSerializer);
    template.setHashValueSerializer(jsonSerializer);

    // 4. Tüm ayarların geçerli olması için post-processing yapılır.
    template.afterPropertiesSet();

    return template;
  }
}