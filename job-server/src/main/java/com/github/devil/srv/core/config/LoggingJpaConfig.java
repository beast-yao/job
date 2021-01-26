package com.github.devil.srv.core.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;

/**
 * @author eric.yao
 * @date 2021/1/18
 **/
@Configuration
@EnableJpaRepositories(basePackages = LoggingJpaConfig.BASE_LOGGING_PACKAGE,
        entityManagerFactoryRef = "loggingEntityManagerFactoryBean",
        transactionManagerRef = "loggingTransactionManager")
public class LoggingJpaConfig {

    public final static String BASE_LOGGING_PACKAGE = "com.github.devil.srv.core.persist.logging";

    @Resource
    private ObjectProvider<HibernateProperties> hibernateProperties;

    @Bean("loggingDataSource")
    @ConfigurationProperties(value = "spring.datasource.logging")
    public DataSource coreDataSource(){
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean("logging-jpa-map")
    @ConfigurationProperties(value = "jpa.logging")
    public Map<String,Object> initJpaProperties(JpaProperties jpaProperties){
        return hibernateProperties
                .getIfAvailable(HibernateProperties::new)
                .determineHibernateProperties(jpaProperties.getProperties(),new HibernateSettings());
    }

    @Bean("loggingEntityManagerFactoryBean")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(@Qualifier("loggingDataSource") DataSource loggingDataSource,
                                                                           EntityManagerFactoryBuilder builder,
                                                                           JpaProperties jpaProperties){
        return builder.dataSource(loggingDataSource)
                .packages(BASE_LOGGING_PACKAGE)
                .properties(initJpaProperties(jpaProperties))
                .persistenceUnit("job-unit-logging")
                .build();
    }

    @Bean("loggingTransactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("loggingEntityManagerFactoryBean") LocalContainerEntityManagerFactoryBean loggingEntityManagerFactoryBean){
        return new JpaTransactionManager(Objects.requireNonNull(loggingEntityManagerFactoryBean.getObject()));
}

}
