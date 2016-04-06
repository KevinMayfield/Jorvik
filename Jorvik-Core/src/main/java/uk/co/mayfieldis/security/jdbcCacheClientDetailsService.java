package uk.co.mayfieldis.security;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

//import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;


public class jdbcCacheClientDetailsService implements ClientDetailsService {

	
	//private JdbcTemplate jdbcTemplate;
	
	private  DataSource dataSource;
	
	private static final Logger log = LoggerFactory.getLogger(jdbcCacheClientDetailsService.class);
	
	final String QUERY_SECURITY_USER = "SELECT ID, "
			+" Disabled,"
			+" Expire,"
			+" MD5Enc,"
			+" Name,"
			+" Password,"
			+" SessionId,"
			+" TYPE,"
			+" Username"
			+" FROM Security.Logon"
			+" WHERE Disabled=1 and Username = ?";
	
	@Autowired
	public void setDataSource(@Qualifier("securityDataSource") DataSource dataSource) {
		//this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.dataSource = dataSource;
	}
	
	
	
	public ClientDetails loadClientByClientId(String ClientId)
			throws ClientRegistrationException {
			// At present do not need this lookup but can be used to set permissions in the client details object
			 log.debug("In ClientDetails class Username="+ClientId);
			 JDBCDBAuthenticationProvider userObj = new JDBCDBAuthenticationProvider();
			 userObj.setDataSource(this.dataSource);
			 User user = userObj.getUserDetail(ClientId);
			 
			 ClientDetails client = new chftClientDetails(user, "", "patient/Observation.read,patient/Patient.read","password,refresh_token");
			 log.debug("client="+client.toString());
			 return client;
	}
	
}
