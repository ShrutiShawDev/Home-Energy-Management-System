package com.shruti.homeenergy.userservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI userServiceApiDocs(){
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("User service API")
                        .description("User service API for Home Energy Management System")
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
