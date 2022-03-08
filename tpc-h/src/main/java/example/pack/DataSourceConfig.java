package example.pack;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean(name = "logDataSource")
    @Qualifier("logDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.log")
    public DataSource logDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "logJdbcTemplate")
    public JdbcTemplate logJdbcTemplate(
            @Qualifier("logDataSource") DataSource dataSource) {

        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "tpchtestDataSource")
    @Qualifier("tpchtestDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.tpchtest")
    public DataSource tpchtestDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "tpchtestJdbcTemplate")
    public JdbcTemplate tpchtestJdbcTemplate(
            @Qualifier("tpchtestDataSource") DataSource dataSource) {

        return new JdbcTemplate(dataSource);
    }
}
