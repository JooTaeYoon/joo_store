package com.joo.pro.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 모든 /api 경로에 CORS 설정을 적용합니다.
        registry.addMapping("/api/**")
                // Vue 프론트엔드가 실행되는 주소를 명시합니다.
                // 개발 환경에서 사용되는 포트를 입력해주세요.
                .allowedOrigins("http://localhost:5173", "http://127.0.0.1:5173")

                // 허용할 HTTP 메서드 (GET, POST 등)를 설정합니다.
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")

                // 모든 헤더를 허용합니다.
                .allowedHeaders("*")

                // 인증 정보(쿠키, 세션 등) 전달을 허용할지 설정합니다.
                // JWT 등을 사용할 경우 true로 설정하는 경우가 많습니다.
                .allowCredentials(true)

                // Pre-flight 요청 결과를 캐시할 시간 (초)을 설정합니다.
                .maxAge(3600);
    }
}
