package edu.java.scrapper.repository;

import liquibase.Scope;
import liquibase.command.CommandScope;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.DirectoryResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.io.File;
import java.nio.file.Path;
import java.sql.DriverManager;

@Testcontainers
@Slf4j
public abstract class IntegrationEnvironment {
    public static PostgreSQLContainer<?> POSTGRES;

    static {
        POSTGRES = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("scrapper")
            .withUsername("postgres")
            .withPassword("postgres");
        POSTGRES.start();

        POSTGRES.waitingFor(Wait.forListeningPort());

        try {
            runMigrations();
        } catch (Exception e) {
            log.error("exception in tests", e);
        }
    }

    @AfterAll
    public static void postgresStop() {
        POSTGRES.stop();
    }

    private static void runMigrations() throws Exception {
        Path migrationsPath =
            new File(".").toPath().toAbsolutePath().
                getParent().getParent().
                resolve("migrations");
        java.sql.Connection connection = DriverManager.getConnection(POSTGRES.getJdbcUrl(),
            POSTGRES.getUsername(), POSTGRES.getPassword()
        );
        Database database =
            DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        Scope.child(Scope.Attr.resourceAccessor, new DirectoryResourceAccessor(migrationsPath), () -> {
            CommandScope update = new CommandScope("update");
            update.addArgumentValue("changelogFile", "master.xml");
            update.addArgumentValue("database", database);
            update.execute();
        });
    }

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        System.out.println(POSTGRES.getJdbcUrl());
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }
}
