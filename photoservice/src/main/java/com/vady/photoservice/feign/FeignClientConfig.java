package com.vady.photoservice.feign;


import feign.RequestInterceptor;
import feign.Response;
import feign.codec.ErrorDecoder;
import io.micrometer.core.instrument.util.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Add logging to debug the request
            System.out.println("Making request to: " + requestTemplate.url());
            
            // If you need to pass authentication tokens, add them here
            // For example, if you're using OAuth2:
            // SecurityContext securityContext = SecurityContextHolder.getContext();
            // if (securityContext != null && securityContext.getAuthentication() != null) {
            //     OAuth2AuthenticationToken auth = (OAuth2AuthenticationToken) securityContext.getAuthentication();
            //     if (auth != null && auth.getCredentials() != null) {
            //         requestTemplate.header("Authorization", "Bearer " + auth.getCredentials());
            //     }
            // }
        };
    }
    
    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }
    
    public class CustomErrorDecoder implements ErrorDecoder {
        private final ErrorDecoder defaultErrorDecoder = new Default();
        
        @Override
        public Exception decode(String methodKey, Response response) {
            System.out.println("Error response from Feign client: " + response.status());
            try {
                String responseBody = IOUtils.toString(response.body().asInputStream(), StandardCharsets.UTF_8);
                System.out.println("Error response body: " + responseBody);
            } catch (IOException e) {
                System.out.println("Could not read response body: " + e.getMessage());
            }
            return defaultErrorDecoder.decode(methodKey, response);
        }
    }
}