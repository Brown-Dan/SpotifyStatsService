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
                System.getenv("JDBC_DATABASE_URL"));
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
        dataSource.setMaximumPoolSize(1000);
        dataSource.setConnectionTimeout(300000);
        dataSource.setIdleTimeout(6000000);
        dataSource.setMaxLifetime(18000000);

        return dataSource;
    }

    @PostConstruct
    public void migrate() {
        Flyway.configure()
                .dataSource(System.getenv("SPRING_DATASOURCE_URL"), System.getenv("SPRING_DATASOURCE_USERNAME"), System.getenv("SPRING_DATASOURCE_PASSWORD"))
                .schemas("StreamingData")
                .baselineOnMigrate(true)
                .locations("classpath:db/migration")
                .table("changelog")
                .load()
                .migrate();
    }
}
