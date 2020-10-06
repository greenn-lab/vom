package vom.server.chaser;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
    basePackages = "vom.server.chaser",
    entityManagerFactoryRef = "chaserEntityManagerFactory",
    transactionManagerRef = "chaserTransactionManager"
)
public class ChaserJpaConfiguration {
  
  @Value("${spring.jpa.show-sql}")
  boolean showSql;
  
  @Value("${spring.jpa.hibernate.ddl-auto}")
  String ddlMode;
  
  
  @Bean
  @ConfigurationProperties("spring.chaser.datasource")
  public DataSource chaserDataSource() {
    return DataSourceBuilder.create().build();
  }
  
  @Bean
  public LocalContainerEntityManagerFactoryBean chaserEntityManagerFactory() {
    final LocalContainerEntityManagerFactoryBean bean =
        new LocalContainerEntityManagerFactoryBean();
    
    final HibernateJpaVendorAdapter vendorAdapter =
        new HibernateJpaVendorAdapter();
    
    vendorAdapter.setDatabase(Database.H2);
    bean.setJpaVendorAdapter(vendorAdapter);
  
    final Map<String, String> props = new HashMap<>(2);
    final String showSqlString = Boolean.toString(showSql);
    props.put("hibernate.show_sql", showSqlString);
    props.put("hibernate.format_sql", showSqlString);
    props.put("hibernate.hbm2ddl.auto", ddlMode);
    bean.setJpaPropertyMap(props);

    bean.setDataSource(chaserDataSource());
    bean.setPackagesToScan("vom.server.chaser");
    
    return bean;
  }
  
  @Bean
  public PlatformTransactionManager chaserTransactionManager() {
    final JpaTransactionManager manager = new JpaTransactionManager();
    manager.setDataSource(chaserDataSource());
    manager.setEntityManagerFactory(
        chaserEntityManagerFactory().getObject()
    );
    
    return manager;
  }
  
}
