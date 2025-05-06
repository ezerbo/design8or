package com.ss.design8or.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
        info = @Info(
                title = "Design8or API",
                version = "2.0",
                description = "Provides endpoints for managing design assignments, users, and notifications.",
                contact = @Contact(
                        name = "Design8or API Support",
                        email = "ezerbo@design8or.com",
                        url = "https://design8or.com/support"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html"
                )
        )
)
public class ApiConfig {
}
