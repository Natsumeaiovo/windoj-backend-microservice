package com.serein.windojgateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.Arrays;

/**
 * @author: serein
 * @date: 2025/2/24 17:32
 * @description: 处理跨域
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsFilter() {
        // 创建一个 CorsConfiguration 对象
        CorsConfiguration config = new CorsConfiguration();
        // 允许所有的 HTTP 方法（GET, POST, PUT, DELETE 等）
        config.addAllowedMethod("*");
        // 允许发送凭证信息（如 Cookie）
        config.setAllowCredentials(true);
        // todo 实际改为线上真实域名、本地域名
        // 允许所有的域名进行跨域请求
        config.setAllowedOriginPatterns(Arrays.asList("*"));
        // 允许所有的请求头
        config.addAllowedHeader("*");

        // 创建一个 UrlBasedCorsConfigurationSource 对象，并设置路径匹配模式
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        // 注册跨域配置，应用于所有路径
        source.registerCorsConfiguration("/**", config);

        // 返回一个新的 CorsWebFilter 对象，使用上述配置
        return new CorsWebFilter(source);
    }
}

