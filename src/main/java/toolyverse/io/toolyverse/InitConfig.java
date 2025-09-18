package toolyverse.io.toolyverse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

// ╔═══════════════════════════════════════════╗
// ║    TODO: Add required annotations here    ║
// ╚═══════════════════════════════════════════╝
//
//@EnableCaching
//@EnableScheduling
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
