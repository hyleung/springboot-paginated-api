package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.sql.DataSource;

/**
 * Service startup.
 */
@SpringBootApplication
@EnableSwagger2
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    @Value("${db.host:localhost}")
    private String dbHost;
    @Value("${db.schema:paginationDemo}")
    private String dbSchema;
    @Value("${db.user:root}")
    private String dbUser;
    @Value("${db.password:secret}")
    private String dbPassword;
    @Value("${db.driver:com.mysql.jdbc.Driver}")
    private String dbDriver;

    public static void main(String[] args) {
        SpringApplication.run(App.class);
    }

    @Bean
    public DataSource dataSource() {
        final String connectionString = String.format("jdbc:mysql://%s/%s?serverTimezone=UTC", dbHost, dbSchema);
        log.info("Connecting to db at {}", connectionString);
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url(connectionString);
        dataSourceBuilder.username(dbUser);
        dataSourceBuilder.password(dbPassword);
        dataSourceBuilder.driverClassName(dbDriver);
        return dataSourceBuilder.build();
    }
}
