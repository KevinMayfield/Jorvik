package uk.co.mayfieldis.jorvik.hl7v2.springconfig;




import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.camel.component.jms.JmsConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:HAPIHL7.properties")
public class ResourceConfig  {
	
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	 
	@Autowired
	protected Environment env;
		
	@Bean(name ="jmsConnectionFactory" )
	public ActiveMQConnectionFactory activeMQConnectionFactory()
	{
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
		/*
		List<String> trusted = new ArrayList<String>();
		trusted.add("org.hl7.fhir.dstu3");
		trusted.add("ca.uhn.hl7v2.model");
		trusted.add("java.util.HashSet");
		
		factory.setTrustedPackages(trusted);*/
		factory.setTrustAllPackages(true);
		return factory;
	}
	
	@Bean(name="pooledConnectionFactory")
	public PooledConnectionFactory pooledConnectionFactory()
	{
		PooledConnectionFactory pooled = new PooledConnectionFactory();
		pooled.setConnectionFactory(activeMQConnectionFactory());
		pooled.setMaxConnections(8);
		return pooled;
	}
	
	@Bean(name="jmsConfig")
	public JmsConfiguration jmsConfiguration()
	{
		JmsConfiguration jmsConfig = new JmsConfiguration();
		jmsConfig.setConnectionFactory(pooledConnectionFactory());
		jmsConfig.setConcurrentConsumers(5);
		return jmsConfig;
	}
			  
	@Bean(name="activemq")
	public ActiveMQComponent activeMQComponent()
	{
		ActiveMQComponent activeMQComponent= new ActiveMQComponent();
		activeMQComponent.setConfiguration(jmsConfiguration());
		return activeMQComponent;	
	}
	  		  		

}
