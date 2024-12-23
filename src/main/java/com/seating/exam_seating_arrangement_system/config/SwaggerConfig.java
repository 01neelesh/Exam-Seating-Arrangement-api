package com.seating.exam_seating_arrangement_system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Seating Arrangement System API")
                        .version("1.0")
                        .description("""
                                 API for generating exam seating arrangements.
                                                           \s
                                                            File Requirements:
                                                            1. Students Excel File:
                                                               - Must be in .xlsx or .xls format
                                                               - Required columns: Name, RollNumber, Class
                                                               - Each column represents seats in vertical order
                                                           \s
                                                            2. Rooms Excel File:
                                                               - Must be in .xlsx or .xls format
                                                               - Required columns: RoomNumber, rows , columns, capacity, student per unit seat
                                                               - Each row represents seats in horizontal order
                                                           \s
                                                            The API will generate a PDF file containing the seating arrangement
                                                            based on the provided data and arrangement type.
                                """)
                        .contact(new Contact()
                                .name("Neelesh Chaturvedi")
                                .email("neeleshchaturvedi233@gmail.com")));
    }
}