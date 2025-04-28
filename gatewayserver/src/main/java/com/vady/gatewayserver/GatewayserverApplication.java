package com.vady.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayserverApplication.class, args);
	}

//	@Bean
//	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//		return builder.routes()
//				.route("commentservice", r -> r.path("/comments/**")
//						.uri("lb://COMMENTSERVICE"))
//				.build();
//	}
	@Bean
	public RouteLocator routeConfig(RouteLocatorBuilder routeLocatorBuilder) {
		return routeLocatorBuilder.routes()
				.route(p -> p.path("/api/comments/**")
						.filters(f -> f.rewritePath("/api/comments(?<segment>/?.*)", "/comments${segment}")
								.circuitBreaker(config -> config.setName("commentsCircuitBreaker").setFallbackUri("forward:/api/contactSupport")))
						.uri("lb://COMMENTSERVICE"))
				.route(p -> p.path("/api/photos/**")
						.filters(f -> f.rewritePath("/api/photos(?<segment>/?.*)", "/photos${segment}")
								.circuitBreaker(config -> config.setName("photosCircuitBreaker").setFallbackUri("forward:/api/contactSupport"))
						)
						.uri("lb://PHOTOSERVICE"))
				.route(p -> p.path("/api/tags/**")
						.filters(f -> f.rewritePath("/api/tags(?<segment>/?.*)", "/tags${segment}")
								.circuitBreaker(config -> config.setName("photosCircuitBreaker").setFallbackUri("forward:/api/contactSupport"))
						)
						.uri("lb://PHOTOSERVICE"))
				.route(p -> p.path("/api/iam/**")
						.filters(f -> f.rewritePath("/api/iam(?<segment>/?.*)", "/${segment}")
								.circuitBreaker(config -> config.setName("iamCircuitBreaker").setFallbackUri("forward:/api/contactSupport")))
						.uri("lb://IAM-SERVICE"))
				.build();

	}

//	@Bean
//	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//		return builder.routes()
//				.route("commentservice", r -> r.path("/api/comments/**")
//						.uri("lb://COMMENTSERVICE"))
//				.build();
//	}
}
