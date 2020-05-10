package com.homelab.springcloudgateway

import com.github.tomakehurst.wiremock.WireMockServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@SpringBootApplication
class SpringCloudGatewayApplication

fun main(args: Array<String>) {
    runApplication<SpringCloudGatewayApplication>(*args)
    stubUserService()
}

fun stubUserService() {
    WireMockServer(8090).start()
}

@Configuration
class Config {
    @Bean
    fun myRoutes(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
                .route { p ->
                    p.path("/get")
                            .filters { f -> f.addRequestHeader("Hello", "World") }
                            .uri("http://localhost:8090")
                }
                .route { p ->
                    p.host("*.hystrix.com")
                            .filters { f ->
                                f.hystrix { config ->
                                    config.name = "mycmd"
                                }
                            }
                            .uri("http://httpbin.org:80")
                }.build()
    }
}
