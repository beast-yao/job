package com.github.devil.srv.core.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

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

    @Resource
    private Environment environment;

    @Primary
    @Bean("coreDataSource")
    @ConfigurationProperties(value = "spring.datasource.core")
    public DataSource coreDataSource(){
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    public Map<String,Object> initJpaProperties(JpaProperties jpaProperties){
        Map<String,Object> defaultMap = hibernateProperties
                .getIfAvailable(HibernateProperties::new)
                .determineHibernateProperties(jpaProperties.getProperties(),new HibernateSettings());
        return Binder.get(environment).bind("jpa.core", Bindable.ofInstance(defaultMap)).get();
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

    @Primary
    @Bean("transactionTemplate")
    public TransactionTemplate transactionTemplate(@Qualifier("transactionManager") PlatformTransactionManager transactionManager){
        return new TransactionTemplate(transactionManager);
    }
}
