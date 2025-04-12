package ru.mooncess.file_service.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsGlobalConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // разрешаем доступ ко всем эндпоинтам
                .allowedOrigins("http://localhost:3000") // откуда можно делать запросы
                .allowedMethods("*") // какие методы разрешены (GET, POST и т.д.)
                .allowedHeaders("*") // какие заголовки разрешены
                .allowCredentials(true); // разрешаем куки и авторизацию
    }
}
