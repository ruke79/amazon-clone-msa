

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter")    
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j")
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    //implementation("org.springframework.cloud:spring-cloud-starter-bus-amqp")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    
    //implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    //implementation("io.jsonwebtoken:jjwt:0.9.1")
    //implementation("com.auth0:java-jwt:3.19.2")
    //implementation("io.micrometer:micrometer-core")
    //implementation("io.micrometer:micrometer-registry-prometheus")
    
    implementation(project(":common"))
    

   implementation(group = "io.jsonwebtoken", name = "jjwt-api", version= "0.12.6")
   implementation(group = "io.jsonwebtoken", name = "jjwt-jackson", version= "0.12.6")
   implementation(group = "io.jsonwebtoken", name = "jjwt-impl", version= "0.12.6")

}

