package com.kaydev.appstore.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(info = @Info(contact = @Contact(name = "Kenneth Osekhuemen", email = "kenneth.osekhuemen@iisysgroup.com", url = "https://iisysgroup.com"), description = "OpenApi documentation for ITEX Ecommerce API", title = "OpenApi specification", version = "1.0", license = @License(name = "Licence name", url = ""), termsOfService = "Terms of service"), servers = {
                @Server(description = "Local ENV", url = "http://localhost:9090/api"),
                @Server(description = "Dev ENV", url = "http://54.203.193.56:9090/api")
}, security = {
                @SecurityRequirement(name = "bearerAuth")
})
@SecurityScheme(name = "bearerAuth", description = "ITEX Store API description", scheme = "bearer", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", in = SecuritySchemeIn.HEADER)
public class SwaggerConfig {

}
