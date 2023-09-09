package online.partyrun.partyrunbattleservice.global.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
public class RedissonConfig {

    @Bean
    @Profile("prd")
    public RedissonClient clusterRedissonClient(@Value("${spring.data.redis.cluster.nodes}") List<String> nodes) {
        Config config = new Config();
        config.useClusterServers()
                .addNodeAddress(nodes.toArray(new String[nodes.size()]));

        return Redisson.create(config);
    }

    @Bean
    @Profile("!prd")
    public RedissonClient singleRedissonClient(@Value("${spring.data.redis.url}") String redisUrl) {
        Config config = new Config();
        config.useSingleServer().setAddress(redisUrl);
        return Redisson.create(config);
    }
}
