package toolyverse.io.toolyverse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

// ╔═══════════════════════════════════════════╗
// ║    TODO: Add required annotations here    ║
// ╚═══════════════════════════════════════════╝
//
@EnableScheduling
@EnableKafka
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAsync
@EnableCaching
@EnableConfigurationProperties
//
@Slf4j
@Component
@Configuration
public class InitConfig implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        log.info("Project initialized successfully.");
    }
}
