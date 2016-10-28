package uk.nhs.leedsth.hapifhir;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.lang3.time.DateUtils;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;

import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import ca.uhn.fhir.rest.server.interceptor.LoggingInterceptor;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;
import uk.nhs.jorvik.provider.DocumentReferenceResourceProvider;
import uk.nhs.jorvik.provider.PatientResourceProvider;

/*
 * 
 * For SQL Server ensure you use the correct jar https://msdn.microsoft.com/en-us/library/ms378422(v=sql.110).aspx
 * 
SQL Server 2012 Settings

jdbc.Driver=com.microsoft.sqlserver.jdbc.SQLServerDriver
jdbc.url=jdbc:sqlserver://localhost:1433;databaseName=HAPI_FHIR_STU3_TEST
jdbc.username=fhirjpa
jdbc.password=fhirjpa
hibernate.dialect=org.hibernate.dialect.SQLServer2012Dialect
hibernate.show_sql=true

MySql

jdbc.Driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/hapifhirstu3
jdbc.username=fhirjpa
jdbc.password=fhirjpa
hibernate.dialect=org.hibernate.dialect.MySQLDialect
hibernate.show_sql=true

 * 
 * 
 * 
 */

	@Configuration
	@EnableTransactionManagement()
	@PropertySource("classpath:HAPIJPA.properties")
	
	public class FhirServerConfigStu3  {
		// extends BaseJavaConfigDstu3
		/**
		 * Configure FHIR properties around the the JPA server via this bean
		 */
		
		@Autowired
		protected Environment env;
		/*
		@Bean()
		public DaoConfig daoConfig() {
			DaoConfig retVal = new DaoConfig();
			retVal.setSubscriptionEnabled(true);
			retVal.setSubscriptionPollDelay(5000);
			retVal.setSubscriptionPurgeInactiveAfterMillis(DateUtils.MILLIS_PER_HOUR);
			retVal.setAllowMultipleDelete(true);
			// Allow external references
			retVal.setAllowExternalReferences(true); 
			retVal.setAllowInlineMatchUrlReferences(true);
			
			// If you are allowing external references, it is recommended to
		    // also tell the server which references actually will be local
			retVal.getTreatBaseUrlsAsLocal().add("http://fhir.leedsth.nhs.uk");
			retVal.getTreatBaseUrlsAsLocal().add(env.getProperty("hapi.TreatBaseUrlsAsLocal"));
			return retVal;
		}
*/
		/**
		 * The following bean configures the database connection. The 'url' property value of "jdbc:derby:directory:jpaserver_derby_files;create=true" indicates that the server should save resources in a
		 * directory called "jpaserver_derby_files".
		 * 
		 * A URL to a remote database could also be placed here, along with login credentials and other properties supported by BasicDataSource.
		 */
		@Bean
		public DataSource dataSource() {
									
			SimpleDriverDataSource retVal = new SimpleDriverDataSource();
		    
		    try {
		     	 @SuppressWarnings("unchecked")
		         Class<? extends Driver> driverClass = (Class<? extends Driver>) Class.forName(env.getProperty("jdbc.Driver"));
		     	retVal.setDriverClass(driverClass);	      
		    } catch (Exception e) {
		     //  log.error("Error loading driver class", e);
		    }
			retVal.setUrl(env.getProperty("jdbc.url"));
			retVal.setUsername(env.getProperty("jdbc.username"));
			retVal.setPassword(env.getProperty("jdbc.password"));
			
			return retVal;
		}


		@Bean
		public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
			LocalContainerEntityManagerFactoryBean retVal = new LocalContainerEntityManagerFactoryBean();
			retVal.setPersistenceUnitName("HAPI_PU");
			retVal.setDataSource(dataSource());
			retVal.setPackagesToScan("uk.nhs.jorvik.entity");
			retVal.setPersistenceProvider(new HibernatePersistenceProvider());
			retVal.setJpaProperties(jpaProperties());
			return retVal;
		}
		/*
		@Bean
		public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
			LocalSessionFactoryBean sfb = new LocalSessionFactoryBean();
			sfb.setDataSource(dataSource());
			sfb.setPackagesToScan("uk.nhs.jorvik.entity");
			//sfb.setMappingResources(new String[] { "Spitter.hbm.xml" });
			sfb.setHibernateProperties(jpaProperties());
			return sfb;
		}
		*/
		@Bean(name="myResourceProvidersDstu3")
		public List<IResourceProvider> resourceProviders()
		{
			List<IResourceProvider> retVal = new ArrayList<IResourceProvider>();
			retVal.add(new PatientResourceProvider());
			retVal.add(new DocumentReferenceResourceProvider());
			return retVal;
		}
		
		
		private Properties jpaProperties() {
			Properties extraProperties = new Properties();
			extraProperties.put("hibernate.dialect",  env.getProperty("hibernate.dialect"));
			//extraProperties.put("hibernate.dialect", org.hibernate.dialect.SQLServerDialect.class.getName());
			extraProperties.put("hibernate.format_sql", "true");
			extraProperties.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
			extraProperties.put("hibernate.hbm2ddl.auto", "update");
			extraProperties.put("hibernate.jdbc.batch_size", "20");
			extraProperties.put("hibernate.cache.use_query_cache", "false");
			extraProperties.put("hibernate.cache.use_second_level_cache", "false");
			extraProperties.put("hibernate.cache.use_structured_entries", "false");
			extraProperties.put("hibernate.cache.use_minimal_puts", "false");
			extraProperties.put("hibernate.search.default.directory_provider", "filesystem");
			// needed to set properties of this directory sudo chmod -R 777 .
			extraProperties.put("hibernate.search.default.indexBase", env.getProperty("lucene.directory"));
			extraProperties.put("hibernate.search.lucene_version", "LUCENE_CURRENT");
			
			// Ideally we want this set to true but need to remove automatic indexing from resources - we possibly need it for Terminology
			//extraProperties.put("hibernate.search.autoregister_listeners", "false");
			// This should be set to manual for a ETL load. 
			extraProperties.put("hibernate.search.indexing_strategy", "manual");
			//extraProperties.put("hibernate.connection.driver.class",
			//		env.getProperty("jdbc.Driver"));

			return extraProperties;
		}

		/**
		 * Do some fancy logging to create a nice access log that has details about each incoming request.
		*/ 
		public IServerInterceptor loggingInterceptor() {
			LoggingInterceptor retVal = new LoggingInterceptor();
			retVal.setLoggerName("fhirtest.access");
			retVal.setMessageFormat(
					"Path[${servletPath}] Source[${requestHeader.x-forwarded-for}] Operation[${operationType} ${operationName} ${idOrResourceName}] UA[${requestHeader.user-agent}] Params[${requestParameters}] ResponseEncoding[${responseEncodingNoDefault}]");
			retVal.setLogExceptions(true);
			retVal.setErrorMessageFormat("ERROR - ${requestVerb} ${requestUrl}");
			return retVal;
		}
		

		/**
		 * This interceptor adds some pretty syntax highlighting in responses when a browser is detected
		*/ 
		@Bean(autowire = Autowire.BY_TYPE)
		public IServerInterceptor responseHighlighterInterceptor() {
			ResponseHighlighterInterceptor retVal = new ResponseHighlighterInterceptor();
			return retVal;
		}
		
		/*
		 * 
		 * KGM removed for now
		@Bean(autowire = Autowire.BY_TYPE)
		public IServerInterceptor subscriptionSecurityInterceptor() {
			SubscriptionsRequireManualActivationInterceptorDstu3 retVal = new SubscriptionsRequireManualActivationInterceptorDstu3();
			return retVal;
		}
		*/
		
		
		@Bean()
		public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
			JpaTransactionManager retVal = new JpaTransactionManager();
			retVal.setEntityManagerFactory(entityManagerFactory);
			return retVal;
		}

		
	}

