package edu.unc.ceccr.chembench.config;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan("kim.ian.springtest")
@Import(PersistenceContext.class)
public class ApplicationContext {

    @Configuration
    @PropertySource("classpath:application.properties")
    static class ApplicationProperties {}

    @Bean
    PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
