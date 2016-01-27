package edu.unc.ceccr.chembench.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.unc.ceccr.chembench.global.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages = {
        "edu.unc.ceccr.chembench.persistence"
})
@EnableTransactionManagement
public class PersistenceContext {
    private static final String[] ENTITY_PACKAGES = {
            "edu.unc.ceccr.chembench.persistence"
    };

    private static final String[] HIBERNATE_PROPERTIES = {
            "hibernate.dialect",
            "hibernate.hbm2ddl.auto",
            "hibernate.ejb.naming_strategy",
            "hibernate.format_sql",
            "hibernate.show_sql"
    };

    /**
     * Creates and configures the HikariCP datasource bean.
     * @param env   The runtime environment of  our application.
     * @return
     */
    @Bean(destroyMethod = "close")
    DataSource dataSource(Environment env) {
        HikariConfig dataSourceConfig = new HikariConfig();
        dataSourceConfig.setDriverClassName(Constants.DATABASE_DRIVER);
        dataSourceConfig.setJdbcUrl(Constants.DATABASE_URL);
        dataSourceConfig.setUsername(Constants.DATABASE_USERNAME);
        dataSourceConfig.setPassword(Constants.CECCR_DATABASE_PASSWORD);
        return new HikariDataSource(dataSourceConfig);
    }

    /**
     * Creates the bean that creates the JPA entity manager factory.
     * @param dataSource    The datasource that provides the database connections.
     * @param env           The runtime environment of  our application.
     * @return
     */
    @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, Environment env) {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactoryBean.setPackagesToScan(ENTITY_PACKAGES);

        Properties jpaProperties = new Properties();
        for (String property : HIBERNATE_PROPERTIES) {
            jpaProperties.put(property, env.getRequiredProperty(property));
        }
        entityManagerFactoryBean.setJpaProperties(jpaProperties);

        return entityManagerFactoryBean;
    }

    /**
     * Creates the transaction manager bean that integrates the used JPA provider with the
     * Spring transaction mechanism.
     * @param entityManagerFactory  The used JPA entity manager factory.
     * @return
     */
    @Bean
    JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}
