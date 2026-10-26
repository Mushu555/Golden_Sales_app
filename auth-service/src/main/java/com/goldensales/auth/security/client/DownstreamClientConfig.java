
package com.goldensales.sales.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.RestClient;

@Configuration
public class DownstreamClientConfig {

    @Bean
    RestClient productRestClient(
            RestClient.Builder builder,
            @Value("${sales.product-service.url}") String productUrl) {
        return createClient(builder, productUrl);
    }

    @Bean
    RestClient customerRestClient(
            RestClient.Builder builder,
            @Value("${sales.customer-service.url}") String customerUrl) {
        return createClient(builder, customerUrl);
    }

    private RestClient createClient(RestClient.Builder builder, String baseUrl) {
        return builder.clone()
                .baseUrl(baseUrl)
                .requestInterceptor((request, body, execution) -> {
                    Authentication authentication =
                            SecurityContextHolder.getContext().getAuthentication();

                    if (authentication instanceof JwtAuthenticationToken jwt) {
                        request.getHeaders().setBearerAuth(
                                jwt.getToken().getTokenValue());
                    }

                    return execution.execute(request, body);
                })
                .build();
    }
}
