package uk.co.spotistats.spotistatsservice;

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
                "jdbc:postgresql://localhost:5432/UserData",
                "admin",
                "password"
        );
        return DSL.using(connection, SQLDialect.POSTGRES);
    }

    @Bean
    public DataSource dataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String userName,
            @Value("${spring.datasource.password}") String password) {

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setSchema("UserData");
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUsername(userName);
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
                .dataSource("jdbc:postgresql://localhost:5432/UserData", "admin", "password")
                .schemas("UserData")
                .baselineOnMigrate(true)
                .locations("classpath:db/migration")
                .table("changelog")
                .load()
                .migrate();
    }
}
