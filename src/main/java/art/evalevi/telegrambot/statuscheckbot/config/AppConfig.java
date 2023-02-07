package art.evalevi.telegrambot.statuscheckbot.config;

import kong.unirest.Config;
import kong.unirest.Unirest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    public static final int TIMEOUT_MILLIS = 6000;

    @Bean
    public Config unirestConfig() {
       return Unirest.config()
               .socketTimeout(TIMEOUT_MILLIS);
    }
}
