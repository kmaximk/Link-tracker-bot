package edu.java.scrapper.repository;

import java.io.File;
import java.nio.file.Path;
import java.sql.DriverManager;
import edu.java.scrapper.domain.jdbc.JdbcAssignmentRepository;
import edu.java.scrapper.domain.jdbc.JdbcChatRepository;
import edu.java.scrapper.domain.jdbc.JdbcLinkRepository;
import liquibase.Scope;
import liquibase.command.CommandScope;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.DirectoryResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import javax.sql.DataSource;

@Testcontainers
@Slf4j
public abstract class IntegrationEnvironment {
    public static PostgreSQLContainer<?> POSTGRES;

    protected static JdbcLinkRepository linkRepository;

    protected static JdbcAssignmentRepository assignmentRepository;

    protected static JdbcChatRepository chatRepository;

    @BeforeAll
    public static void start() {
        POSTGRES = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("scrapper")
            .withUsername("postgres")
            .withPassword("postgres");
        POSTGRES.start();

        try {
            runMigrations();
        } catch (Exception e) {
            log.error("exception in tests", e);
        }
        DataSource source = DataSourceBuilder.create()
            .url(POSTGRES.getJdbcUrl())
            .password(POSTGRES.getPassword())
            .username(POSTGRES.getUsername())
            .build();
        JdbcClient jdbcClient = JdbcClient.create(source);
        linkRepository = new JdbcLinkRepository(jdbcClient);
        assignmentRepository = new JdbcAssignmentRepository(jdbcClient);
        chatRepository = new JdbcChatRepository(jdbcClient);
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
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }
}
