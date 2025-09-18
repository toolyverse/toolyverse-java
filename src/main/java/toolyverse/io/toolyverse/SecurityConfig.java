package toolyverse.io.toolyverse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityProperties securityProperties;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> securityProperties.getIgnoredPaths()
                .forEach(path -> web.ignoring().requestMatchers(path));
    }

}