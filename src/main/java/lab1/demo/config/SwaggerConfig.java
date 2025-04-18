package lab1.demo.config;



import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Lab API",
                version = "1.0",
                description = "Документация по лабораторной работе"
        )
)
public class SwaggerConfig {
}
