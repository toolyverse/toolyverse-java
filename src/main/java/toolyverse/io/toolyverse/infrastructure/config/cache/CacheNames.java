package toolyverse.io.toolyverse.infrastructure.config.cache;

import java.util.Arrays;
import java.util.List;

public class CacheNames {

    public static final String CACHE_USER_INFO = "user_info";
    public static final String CACHE_USER_PHOTO = "user_photo";

    public static List<String> getAllCacheNames() {
        return Arrays.asList(
                CACHE_USER_INFO,
                CACHE_USER_PHOTO
        );
    }
}