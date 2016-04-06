package uk.co.mayfieldis.security;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;




//https://codesilo.wordpress.com/2012/07/08/mongodb-spring-data-and-spring-security-with-custom-userdetailsservice/


public class JDBCDBAuthenticationProvider implements UserDetailsService {

	
	private JdbcTemplate jdbcTemplate;
	
	private static final Logger log = LoggerFactory.getLogger(JDBCDBAuthenticationProvider.class);
	
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
	
	public UserDetails loadUserByUsername(String username)
			 throws UsernameNotFoundException {
			 log.info("1. In UserDetails class Username="+username);
			 User user = getUserDetail(username);
			 log.info("2. User="+user.toString());
			 return user;
			 }
	
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		// TODO Auto-generated method stub

	}
	
	@Autowired
	 public void setDataSource(@Qualifier("securityDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	 }
	
	public User getUserDetail(String username){
		 
		 log.info("In getUserDetails class Username="+username);
		 
		 Users user = (Users) jdbcTemplate.queryForObject(		
				 QUERY_SECURITY_USER, new Object[] { username }, new UsersRowMapper());
		 
		 
		 User sUser = null;
		 if(user == null){
	            throw new UsernameNotFoundException(username);
	        }
		 else
		 {
			 log.info("In getUserDetails-2 class Username="+username);
			 sUser = new User(user.getUserName(), user.getPassword(), user.getRoles());
			 log.info("User="+user.toString());
		 }
		 return sUser;
		 }
	/*
	protected UserDetails retrieveUser(String userName,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		UserDetails loadedUser;

        try {
        	log.info("In retrieveUser class Username="+userName);
        	Users client = (Users) jdbcTemplate.queryForObject(		
   				 QUERY_SECURITY_USER, new Object[] { userName }, new UsersRowMapper());
            
            loadedUser = new User(client.getUserName(), client.getPassword(), client.getRoles());
        } catch (Exception repositoryProblem) {
            throw new InternalAuthenticationServiceException(repositoryProblem.getMessage(), repositoryProblem);
        }

        return loadedUser;
	}
	 */

	
}
