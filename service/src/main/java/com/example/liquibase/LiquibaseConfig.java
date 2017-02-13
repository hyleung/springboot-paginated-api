package com.example.liquibase;

import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Created by hyleung on 2016-08-04.
 */
@Configuration
public class LiquibaseConfig {
    private static final Logger log = LoggerFactory.getLogger(LiquibaseConfig.class);

    @Value("${mysql.service.host:localhost}")
    private String dbHost;
    @Value("${db.schema:paginationDemo}")
    private String dbSchema;
    @Value("${db.user:root}")
    private String dbUser;
    @Value("${db.password:secret}")
    private String dbPassword;
    @Value("${db.driver:com.mysql.jdbc.Driver}")
    private String dbDriver;
    @Value("${db.maxRetry:5}")
    private int dbMaxRetry;

    @Autowired
    private Environment env;

    private DataSource datasource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        final String connectionString = String.format("jdbc:mysql://%s/%s?serverTimezone=UTC&createDatabaseIfNotExist=true",
                dbHost,
                dbSchema);

        log.info("Liquibase connection string: {} as {}", connectionString, dbUser);

        dataSource.setDriverClassName(dbDriver);
        dataSource.setUsername(dbUser);
        dataSource.setPassword(dbPassword);
        dataSource.setUrl(connectionString);
        return dataSource;
    }

    @Bean
    public SpringLiquibase liquibase() throws InterruptedException {
        final DataSource dataSource = datasource();
        int retryCount = 0;
        while (true) {
            try {
                dataSource.getConnection();
                SpringLiquibase liquibase = new SpringLiquibase();
                liquibase.setDataSource(dataSource);
                liquibase.setChangeLog("classpath:/db/changelog/db.changelog-master.yaml");
                return liquibase;
            } catch (SQLException e) {
                log.warn("Failed to get DB connection on attempt {} of {}, retrying in 5s", retryCount, dbMaxRetry);
                retryCount++;
                if (retryCount > dbMaxRetry) {
                    throw new RuntimeException("Unable to connect to database aborting after " + retryCount + " attempts.");
                }
            }
            Thread.sleep(5000);
        }
    }
}
