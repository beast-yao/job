package com.github.devil.srv.core.config;

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
import org.springframework.context.annotation.Primary;
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
 * @date 2021/1/22
 **/
@Configuration
@EnableJpaRepositories(basePackages = CoreJpaConfig.BASE_CORE_PACKAGE,
        entityManagerFactoryRef = "entityManagerFactoryBean",
        transactionManagerRef = "transactionManager")
public class CoreJpaConfig{

    public final static String BASE_CORE_PACKAGE = "com.github.devil.srv.core.persist.core";

    @Resource
    private ObjectProvider<HibernateProperties> hibernateProperties;

    @Primary
    @Bean("coreDataSource")
    @ConfigurationProperties(value = "spring.datasource.core")
    public DataSource coreDataSource(){
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean("core-jpa-map")
    @ConfigurationProperties(value = "jpa.core")
    public Map<String,Object> initJpaProperties(JpaProperties jpaProperties){
        return hibernateProperties
                .getIfAvailable(HibernateProperties::new)
                .determineHibernateProperties(jpaProperties.getProperties(),new HibernateSettings());
    }

    @Primary
    @Bean("entityManagerFactoryBean")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(DataSource coreDataSource,
                                                                           EntityManagerFactoryBuilder builder,
                                                                           JpaProperties jpaProperties){
        return builder.dataSource(coreDataSource)
                .packages(BASE_CORE_PACKAGE)
                .properties(initJpaProperties(jpaProperties))
                .persistenceUnit("job-unit-core")
                .build();
    }

    @Primary
    @Bean("transactionManager")
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactoryBean){
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactoryBean.getObject()));
    }
}
