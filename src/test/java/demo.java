
import com.reji.RegisApplication;
import com.reji.bean.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = RegisApplication.class)
public class demo {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CacheManager cacheManager;

    @CachePut(value = "",key = "#user.id")
    @Test
    public void Redis(User user){

    }
}
