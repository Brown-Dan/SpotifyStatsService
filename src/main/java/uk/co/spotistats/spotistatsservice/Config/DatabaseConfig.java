package uk.co.spotistats.spotistatsservice.Config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class DatabaseConfig {

    @Bean
    public DSLContext dslContext() throws SQLException {

        Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://ec2-34-250-119-127.eu-west-1.compute.amazonaws.com:5432/db1u7m6u2v1m60?password=pdf4b7fc466948969fd029adb11a0cb3002d1a9e6a07d529e2503760f2a2da642&amp;sslmode=require&amp;user=ub3kreaapns63l", "ub3kreaapns63l", "pdf4b7fc466948969fd029adb11a0cb3002d1a9e6a07d529e2503760f2a2da642");
        return DSL.using(connection, SQLDialect.POSTGRES);
    }

    @Bean
    public DataSource dataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password) {

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setSchema("StreamingData");
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setJdbcUrl(url);
        dataSource.setMaximumPoolSize(5);
        dataSource.setConnectionTimeout(300000);
        dataSource.setIdleTimeout(600000);
        dataSource.setMaxLifetime(1800000);

        return dataSource;
    }

    @PostConstruct
    public void migrate() {
        Flyway.configure()
                .dataSource("jdbc:postgresql://ec2-34-250-119-127.eu-west-1.compute.amazonaws.com:5432/db1u7m6u2v1m60?password=pdf4b7fc466948969fd029adb11a0cb3002d1a9e6a07d529e2503760f2a2da642&amp;sslmode=require&amp;user=ub3kreaapns63l", "ub3kreaapns63l", "pdf4b7fc466948969fd029adb11a0cb3002d1a9e6a07d529e2503760f2a2da642")
                .schemas("StreamingData")
                .baselineOnMigrate(true)
                .locations("classpath:db/migration")
                .table("changelog")
                .load()
                .migrate();
    }
}
