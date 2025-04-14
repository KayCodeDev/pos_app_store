package com.kaydev.appstore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kaydev.appstore.handlers.StoreInterceptor;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {

    private final StoreInterceptor storeInterceptor;

    private static final String[] STORE_URL = {
            "/v1/store/**",
    };

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(storeInterceptor)
                .addPathPatterns(STORE_URL);
    }
}