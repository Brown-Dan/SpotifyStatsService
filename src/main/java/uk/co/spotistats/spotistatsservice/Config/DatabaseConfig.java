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
                "jdbc:postgresql://ep-shrill-snow-a2lajvf5.eu-central-1.aws.neon.tech:5432/StreamingData?sslmode=require",
                "admin",
                "e9I5ysuZQXtn"
        );
        return DSL.using(connection, SQLDialect.POSTGRES);
    }

    @Bean
    public DataSource dataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String userName,
            @Value("${spring.datasource.password}") String password) {

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setSchema("StreamingData");
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
                .dataSource("jdbc:postgresql://ep-shrill-snow-a2lajvf5.eu-central-1.aws.neon.tech:5432/StreamingData?sslmode=require", "admin", "e9I5ysuZQXtn")
                .schemas("StreamingData")
                .baselineOnMigrate(true)
                .locations("classpath:db/migration")
                .table("changelog")
                .load()
                .migrate();
    }
}
