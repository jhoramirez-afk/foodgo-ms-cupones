package cl.duoc.jv0101.foodgo.cupones.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cupon API")
                        .version("2.0.0")
                        .description("Microservicio Cupon del caso FoodGo - EP02 JVY0101."));
    }
}
