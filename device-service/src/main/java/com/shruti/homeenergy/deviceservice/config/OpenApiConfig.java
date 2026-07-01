package com.shruti.homeenergy.deviceservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI deviceServiceApiDocs(){
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Device service API")
                        .description("Device service API for Home Energy Management System")
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
