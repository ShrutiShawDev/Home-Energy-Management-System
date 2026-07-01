package com.shruti.homeenergy.usageservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI usageServiceApiDocs(){
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Usage service API")
                        .description("Usage service API for Home Energy Management System")
                        .contact(getContact())
                        .version("1.0.0")
                );
    }

    private static Contact getContact(){
        Contact contact = new Contact();
        contact.setEmail("shrutishaw750@gmail.com");
        return contact;
    }
}
