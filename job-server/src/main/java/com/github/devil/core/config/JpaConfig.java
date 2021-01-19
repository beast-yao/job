package com.github.devil.core.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.ObjectProvider;
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
@EnableJpaRepositories(basePackages = JpaConfig.BASE_PACKAGE,
        entityManagerFactoryRef = "entityManagerFactoryBean",
        transactionManagerRef = "transactionManager")
public class JpaConfig {

    public final static String BASE_PACKAGE = "com.github.devil.core.persist";

    @Resource
    private ObjectProvider<HibernateProperties> hibernateProperties;

    @Bean("dataSource")
    @ConfigurationProperties(value = "spring.datasource.derby")
    public DataSource dataSource(){
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    public Map<String,Object> initJpaProperties(JpaProperties jpaProperties){
        return hibernateProperties
                .getIfAvailable(HibernateProperties::new)
                .determineHibernateProperties(jpaProperties.getProperties(),new HibernateSettings());
    }

    @Bean("entityManagerFactoryBean")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(DataSource dataSource,
                                                                           EntityManagerFactoryBuilder builder,
                                                                           JpaProperties jpaProperties){
        return builder.dataSource(dataSource)
                .packages(BASE_PACKAGE)
                .properties(initJpaProperties(jpaProperties))
                .persistenceUnit("job-unit")
                .build();
    }

    @Bean("transactionManager")
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactoryBean){
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactoryBean.getObject()));
    }
}
