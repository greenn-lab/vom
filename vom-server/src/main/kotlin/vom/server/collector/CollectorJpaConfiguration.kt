package vom.server.collector

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.Database
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import java.util.*
import javax.sql.DataSource

@Configuration
@ConfigurationProperties("collector.datasource")
@EnableJpaRepositories(
  basePackages = ["vom.server.collector"],
  entityManagerFactoryRef = "collectorEntityManagerFactory",
  transactionManagerRef = "collectorTransactionManager")
class CollectorJpaConfiguration {


  @Value("\${spring.jpa.show-sql}")
  var showSql = true

  @Value("\${spring.jpa.hibernate.ddl-auto}")
  lateinit var ddlMode: String

  lateinit var driverClassName: String

  lateinit var jdbcUrl: String

  @Bean
  fun collectorDataSource(): DataSource {
    return DataSourceBuilder.create()
      .driverClassName(driverClassName)
      .url(jdbcUrl)
      .username("sa")
      .password("").build()
  }

  @Bean
  fun collectorEntityManagerFactory(): LocalContainerEntityManagerFactoryBean {
    val bean = LocalContainerEntityManagerFactoryBean()
    val vendorAdapter = HibernateJpaVendorAdapter()
    vendorAdapter.setDatabase(Database.H2)
    bean.jpaVendorAdapter = vendorAdapter
    val props: MutableMap<String, String?> = HashMap(3)
    props["hibernate.show_sql"] = showSql.toString()
    props["hibernate.format_sql"] = showSql.toString()
    props["hibernate.hbm2ddl.auto"] = ddlMode
    bean.setJpaPropertyMap(props)
    bean.dataSource = collectorDataSource()
    bean.setPackagesToScan("vom.server.collector")
    return bean
  }

  @Bean
  fun collectorTransactionManager(): PlatformTransactionManager? {
    val manager = JpaTransactionManager()
    manager.dataSource = collectorDataSource()
    manager.entityManagerFactory = collectorEntityManagerFactory().getObject()
    return manager
  }

}

