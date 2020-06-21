package com.ebayil.meetingservice.config;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;
import java.util.Collections;

import static com.google.common.base.Predicates.equalTo;

@Configuration
@EnableSwagger2
public class SwaggerConfig
{
    @Autowired
    private TypeResolver typeResolver;

    @Bean
    public Docket swaggerSpringMvcPlugin() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.ebayil.meetingservice"))
                .paths( PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .directModelSubstitute(LocalDate.class,
                        String.class)
                ;
    }

    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo("Meetings Service", "Ariel Malik, for Ebay-IL, Hagai, Task 1", "1.0",
                "Test Application",
                new Contact("relrtl@gmail.com", "relrtl@gmail.com", "Ariel Malik"),
                "Free for use", "Free for use", Collections.EMPTY_LIST);
        return apiInfo;
    }
}
