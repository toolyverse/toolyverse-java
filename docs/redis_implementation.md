# Spring Boot Redis Cache Configuration

A comprehensive guide for implementing Redis-based caching in Spring Boot applications with multiple TTL configurations.

## Dependencies

Add the following dependencies to your `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>

<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
    <version>2.20.0</version>
</dependency>
```

## Configuration

### Application Properties

Configure Redis connection in your `application.yml`:

```yaml
spring:
  application:
    data:
      redis:
        host: ${REDIS_HOST}
        port: ${REDIS_PORT}
```

### Enable Caching

Add `@EnableCaching` to your configuration class:

```java
@EnableCaching
@Slf4j
@Component
@Configuration
public class InitConfig implements CommandLineRunner {
    // Your initialization logic
}
```

## Cache Names Definition

Create a centralized cache names registry:

```java
public class CacheNames {
    public static final String CACHE_USER_INFO = "user_info";
    public static final String CACHE_USER_PHOTO = "user_photo";
    public static final String CACHE_ALL_LOOKUPS = "all_lookups";
    
    public static List<String> getAllCacheNames() {
        return Arrays.asList(
                CACHE_USER_INFO,
                CACHE_USER_PHOTO,
                CACHE_ALL_LOOKUPS
        );
    }
}
```

## Redis Cache Configuration

### Main Configuration Class

```java
@Slf4j
@EnableCaching
@Configuration
@RequiredArgsConstructor
public class RedisCacheConfig {
    
    // Cache Manager Bean Names
    public static final String REDIS_CACHE_MANAGER_ONE_MINUTE = "redisOneMinuteCacheManager";
    public static final String REDIS_CACHE_MANAGER_TWO_MINUTES = "redisTwoMinutesCacheManager";
    public static final String REDIS_CACHE_MANAGER_FIVE_MINUTES = "redisFiveMinutesCacheManager";
    public static final String REDIS_CACHE_MANAGER_TEN_MINUTES = "redisTenMinutesCacheManager";
    public static final String REDIS_CACHE_MANAGER_THIRTY_MINUTES = "redisThirtyMinutesCacheManager";
    
    private final ObjectMapper objectMapper;
    
    // Configuration methods (see full code above)
}
```

### ObjectMapper Configuration

```java
@Configuration
public class ObjectMapperConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}
```

## Usage Examples

### Basic Cache Annotations

#### @Cacheable - Cache the result
```java
@Cacheable(value = CacheNames.CACHE_USER_INFO, key = "#userId", 
           cacheManager = RedisCacheConfig.REDIS_CACHE_MANAGER_FIVE_MINUTES)
public User getUserInfo(String userId) {
    // Method implementation
}
```

#### @CachePut - Update cache
```java
@CachePut(value = CacheNames.CACHE_USER_INFO, key = "#user.id", 
          cacheManager = RedisCacheConfig.REDIS_CACHE_MANAGER_FIVE_MINUTES)
public User updateUser(User user) {
    // Method implementation
}
```

#### @CacheEvict - Remove from cache
```java
// Single entry eviction
@CacheEvict(value = CacheNames.CACHE_USER_INFO, key = "#userId", 
            cacheManager = RedisCacheConfig.REDIS_CACHE_MANAGER_FIVE_MINUTES)
public void deleteUser(String userId) {
    // Method implementation
}

// Clear all entries
@CacheEvict(value = CacheNames.CACHE_USER_INFO, allEntries = true, 
            cacheManager = RedisCacheConfig.REDIS_CACHE_MANAGER_FIVE_MINUTES)
public void clearAllUsersCache() {
    log.info("Clearing all users from cache");
}
```

### Direct Redis Operations with StringRedisTemplate

```java
@Service
@RequiredArgsConstructor
public class OtpService {
    private final StringRedisTemplate stringRedisTemplate;
    
    public void saveOtp(String userId, String otpCode) {
        String redisKey = "otp:" + userId;
        
        // Check existing OTP
        String existingOtp = stringRedisTemplate.opsForValue().get(redisKey);
        
        // Save with expiry
        stringRedisTemplate.opsForValue().set(redisKey, otpCode, Duration.ofMinutes(5));
        
        // Alternative syntax
        stringRedisTemplate.opsForValue().set(redisKey, otpCode, 5, TimeUnit.MINUTES);
    }
}
```

## Available Cache Managers

The configuration provides five cache managers with different TTL settings:

| Cache Manager | TTL | Constant Name |
|--------------|-----|---------------|
| One Minute | 1 minute | `REDIS_CACHE_MANAGER_ONE_MINUTE` |
| Two Minutes | 2 minutes | `REDIS_CACHE_MANAGER_TWO_MINUTES` |
| Five Minutes | 5 minutes | `REDIS_CACHE_MANAGER_FIVE_MINUTES` |
| Ten Minutes | 10 minutes | `REDIS_CACHE_MANAGER_TEN_MINUTES` |
| Thirty Minutes | 30 minutes | `REDIS_CACHE_MANAGER_THIRTY_MINUTES` |

## Features

- **Multiple TTL Options**: Choose the appropriate cache duration for different use cases
- **JSON Serialization**: Automatic serialization/deserialization using Jackson
- **Type Safety**: Strongly-typed cache names via constants
- **Flexible Access**: Both annotation-based and programmatic cache access
- **Production Ready**: Includes proper logging and configuration

## Best Practices

1. Use shorter TTLs for frequently changing data
2. Use longer TTLs for relatively stable reference data
3. Always specify explicit cache keys to avoid collisions
4. Use `@CacheEvict` when data is updated or deleted
5. Monitor cache hit/miss ratios in production