package com.mitocode;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class GatewayConfig {


    //MODO FUNCIONAL DEL GATEWAY
		/*@Bean
	    public RouteLocator routeLocator(RouteLocatorBuilder builder, TokenRelayGatewayFilterFactory tokenRelay) {
	        return builder.routes()
	                .route("m1-service", r -> r.path("/m1/**")
	                		.filters(f -> {
	                			f.rewritePath("/m1/(?<path>.*)", "/${path}");
	                			return f.filter(tokenRelay.apply());
	                		})
	                        .uri("http://localhost:8080"))
	                .route("m2-service", r -> r.path("/m2/**")
	                		.filters(f -> {
	                			f.rewritePath("/m2/(?<path>.*)", "/${path}");
	                			return f.filter(tokenRelay.apply());
	                		})
	                        .uri("lb://micro2"))
	                .build();
	    }*/
}
