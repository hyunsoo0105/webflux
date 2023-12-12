package com.example.webflux.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.core.DefaultReactiveDataAccessStrategy;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.MySqlDialect;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.ReactiveTransactionManager;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;

@Configuration
@EnableR2dbcAuditing
@EnableR2dbcRepositories(basePackages = "com.example.webflux.**.reactive", entityOperationsRef = "r2dbcEntityOperations", repositoryImplementationPostfix = "*ReactiveRepository")
public class R2dbcConfig {
    @Primary
    @Bean(name = "r2dbcProperties")
    public R2dbcProperties r2dbcProperties() {
        return new R2dbcProperties();
    }

    @Primary
    @Bean(name = "r2dbcConnectionFactory")
    public ConnectionFactory r2dbcConnectionFactory(@Qualifier("r2dbcProperties") R2dbcProperties r2dbcProperties) {
        return ConnectionFactories.get(ConnectionFactoryOptions.parse(r2dbcProperties.getUrl())
                .mutate()
                .option(ConnectionFactoryOptions.USER, r2dbcProperties.getUsername())
                .option(ConnectionFactoryOptions.PASSWORD, r2dbcProperties.getPassword())
                .build());
    }

    @Primary
    @Bean(name = "r2dbcEntityOperations")
    public R2dbcEntityOperations r2dbcEntityOperations(@Qualifier("r2dbcConnectionFactory") ConnectionFactory connectionFactory) {
        DefaultReactiveDataAccessStrategy strategy = new DefaultReactiveDataAccessStrategy(MySqlDialect.INSTANCE);
        DatabaseClient databaseClient = DatabaseClient.builder()
                .connectionFactory(connectionFactory)
                .build();

        return new R2dbcEntityTemplate(databaseClient, strategy);
    }

    @Primary
    @Bean(name = "r2dbcTransactionManager")
    public ReactiveTransactionManager r2dbcTransactionManager(@Qualifier("r2dbcConnectionFactory") ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }

    /* sql로 table 등을 생성할때 필요 */
    @Bean
    ConnectionFactoryInitializer initializer(@Qualifier("r2dbcConnectionFactory") ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("sql/r2dbc_table_schema.sql")));
        return initializer;
    }
}