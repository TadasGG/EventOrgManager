package lt.bropro.inventorymanager.server.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Tadas Grigaitis",
                        email = "grigaitis.t@gmail.com"
                ),
                description = "OpenAPI application to access the inventory manager database.",
                title = "Inventory Management Application",
                version = "1.0",
                license = @License(
                        name = "Open Domain"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Inventory management database.")
        }
)
public class OpenApiConfig {

}
