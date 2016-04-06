package uk.co.mayfieldis.security;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;


// http://docs.spring.io/spring-security/oauth/xref/org/springframework/security/oauth2/provider/token/store/JdbcTokenStore.html


public class CustomTokenStore implements TokenStore {
		
	  
	  	//
	
	  	private static final String DEFAULT_ACCESS_TOKEN_SELECT_STATEMENT = "select TokenKey, AccessToken from Security.OAuth2AccessToken WHERE TokenKey = ?";

	  	private static final String DEFAULT_ACCESS_TOKEN_DELETE_STATEMENT = "delete from Security.OAuth2AccessToken where TokenKey = ?";
	  	
		private static final String INSERT_ACCESS_TOKEN_SQL = "INSERT INTO Security.OAuth2AccessToken(Username, Authentication, Expiration, BearerType, TokenKey, Value, RefreshTokenKey, Scopes, AccessToken)"
				+" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		private static final String INSERT_REFRESH_TOKEN_SQL = "INSERT INTO Security.OAuth2RefreshToken(Username, Authentication, RefreshTokenKey)"
				+" VALUES (?, ?, ?)";

		private static final String DEFAULT_ACCESS_TOKEN_AUTHENTICATION_SELECT_STATEMENT = "select TokenKey, Authentication from Security.OAuth2AccessToken WHERE TokenKey = ?";
		
		
	
	  	/*
	  
	  	private static final String DEFAULT_ACCESS_TOKEN_FROM_AUTHENTICATION_SELECT_STATEMENT = "select token_id, token from oauth_access_token where authentication_id = ?";
	  
	  	private static final String DEFAULT_ACCESS_TOKENS_FROM_USERNAME_AND_CLIENT_SELECT_STATEMENT = "select token_id, token from oauth_access_token where user_name = ? and client_id = ?";
	  
	  	private static final String DEFAULT_ACCESS_TOKENS_FROM_USERNAME_SELECT_STATEMENT = "select token_id, token from oauth_access_token where user_name = ?";
	  
	  	private static final String DEFAULT_ACCESS_TOKENS_FROM_CLIENTID_SELECT_STATEMENT = "select token_id, token from oauth_access_token where client_id = ?";
	  
	  	
	  
	  	private static final String DEFAULT_ACCESS_TOKEN_DELETE_FROM_REFRESH_TOKEN_STATEMENT = "delete from oauth_access_token where refresh_token = ?";
	  
	  	private static final String DEFAULT_REFRESH_TOKEN_INSERT_STATEMENT = "insert into oauth_refresh_token (token_id, token, authentication) values (?, ?, ?)";
	  
	  	private static final String DEFAULT_REFRESH_TOKEN_SELECT_STATEMENT = "select token_id, token from oauth_refresh_token where token_id = ?";
	  
	  	private static final String DEFAULT_REFRESH_TOKEN_AUTHENTICATION_SELECT_STATEMENT = "select token_id, authentication from oauth_refresh_token where token_id = ?";
	  
	  	private static final String DEFAULT_REFRESH_TOKEN_DELETE_STATEMENT = "delete from oauth_refresh_token where token_id = ?";
	  */
	  	private String insertAccessTokenSql = INSERT_ACCESS_TOKEN_SQL;
	  
	  	private String selectAccessTokenSql = DEFAULT_ACCESS_TOKEN_SELECT_STATEMENT;
	  
	  	private String selectAccessTokenAuthenticationSql = DEFAULT_ACCESS_TOKEN_AUTHENTICATION_SELECT_STATEMENT;
	  
	  	/*
	  	private String selectAccessTokenFromAuthenticationSql = DEFAULT_ACCESS_TOKEN_FROM_AUTHENTICATION_SELECT_STATEMENT;
	  
	  	private String selectAccessTokensFromUserNameAndClientIdSql = DEFAULT_ACCESS_TOKENS_FROM_USERNAME_AND_CLIENT_SELECT_STATEMENT;
	  
	  	private String selectAccessTokensFromUserNameSql = DEFAULT_ACCESS_TOKENS_FROM_USERNAME_SELECT_STATEMENT;
	  
	  	private String selectAccessTokensFromClientIdSql = DEFAULT_ACCESS_TOKENS_FROM_CLIENTID_SELECT_STATEMENT;
	  */
	  	private String deleteAccessTokenSql = DEFAULT_ACCESS_TOKEN_DELETE_STATEMENT;
	  /*
	  	private String insertRefreshTokenSql = DEFAULT_REFRESH_TOKEN_INSERT_STATEMENT;
	  
	  	private String selectRefreshTokenSql = DEFAULT_REFRESH_TOKEN_SELECT_STATEMENT;
	  
	  	private String selectRefreshTokenAuthenticationSql = DEFAULT_REFRESH_TOKEN_AUTHENTICATION_SELECT_STATEMENT;
	  
	  	private String deleteRefreshTokenSql = DEFAULT_REFRESH_TOKEN_DELETE_STATEMENT;
	  
	  	private String deleteAccessTokenFromRefreshTokenSql = DEFAULT_ACCESS_TOKEN_DELETE_FROM_REFRESH_TOKEN_STATEMENT;
	  	*/
	private JdbcTemplate jdbcTemplate;
	
	

	
	private static final Logger log = LoggerFactory.getLogger(CustomTokenStore.class);
	
	@Autowired
	 public void setDataSource(@Qualifier("securityDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	 }
	
	
	public Collection<OAuth2AccessToken> findTokensByClientId(String ClientId) {
		log.info("findTokensByClientId ClientId="+ClientId);
		return null;
	}

	public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(
			String ClientId, String UserName) {
		log.info("findTokensByClientIdAndUserName ClientId="+ClientId+" "+UserName); 
		return null;
	}

	public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
		log.info("getAccessToken OAuth2Authentication="+authentication.toString());
		return null;
	}

	public OAuth2AccessToken readAccessToken(String tokenValue) {
		log.info("readAccessToken String="+tokenValue);
		//log.info("readAccessToken extractTokenKey="+extractTokenKey(tokenValue));
		OAuth2AccessToken accessToken = null;
		 
 		try {
 			accessToken = jdbcTemplate.queryForObject(selectAccessTokenSql, new RowMapper<OAuth2AccessToken>() {
 				public OAuth2AccessToken mapRow(ResultSet rs, int rowNum) throws SQLException {
 					return deserializeAccessToken(rs.getBytes(2));
 				}
 			}, tokenValue);
 		}
 		catch (EmptyResultDataAccessException e) {
 			log.info(e.getMessage());
 			if (log.isInfoEnabled()) {
 				log.info("Failed to find access token for token " + tokenValue);
 			}
 		}
 		catch (IllegalArgumentException e) {
 			log.warn("Failed to deserialize access token for " + tokenValue, e);
 			removeAccessToken(tokenValue);
 		}
 
 		return accessToken;
	}

	public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
		log.info("readAuthentication OAuth2AccessToken="+token.toString());
		//return readAuthentication(extractTokenKey(token.getValue()));
		return readAuthentication(token.getValue());
	}

	public OAuth2Authentication readAuthentication(String token) {
		log.info("readAuthentication String"+token);
		
		OAuth2Authentication authentication = null;

 		try {
 			authentication = jdbcTemplate.queryForObject(selectAccessTokenAuthenticationSql,
 					new RowMapper<OAuth2Authentication>() {
 						public OAuth2Authentication mapRow(ResultSet rs, int rowNum) throws SQLException {
 							return deserializeAuthentication(rs.getBytes(2));
 						}
 					}, token);
 		}
 		catch (EmptyResultDataAccessException e) {
 			if (log.isInfoEnabled()) {
 				log.info("Failed to find access token for token " + token);
 			}
 		}
 		catch (IllegalArgumentException e) {
 			log.warn("Failed to deserialize authentication for " + token, e);
 			removeAccessToken(token);
 		}
 		log.info("authentication returned"+authentication.toString());
 		return authentication;
	}

	public OAuth2Authentication readAuthenticationForRefreshToken(
			OAuth2RefreshToken token) {
		log.info("readAuthenticationForRefreshToken OAuth2RefreshToken="+token.toString());
		
 		return null;
	}

	public OAuth2RefreshToken readRefreshToken(String str) {
		// TODO Auto-generated method stub
		log.info("readRefreshToken str="+str);
		return null;
	}

	public void removeAccessToken(OAuth2AccessToken token) {
		// TODO Auto-generated method stub
		log.info("removeAccessToken OAuth2AccessToken="+token.toString());
	}

	public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken token) {
		// TODO Auto-generated method stub
		log.info("removeAccessTokenUsingRefreshToken OAuth2RefreshToken="+token.toString());
	}

	public void removeRefreshToken(OAuth2RefreshToken token) {
		// TODO Auto-generated method stub
		 log.info("removeRefreshToken OAuth2RefreshToken="+token.toString());
	}

	public void storeAccessToken(OAuth2AccessToken token,
			OAuth2Authentication authentication) {
		// TODO Auto-generated method stub
		 log.info("storeAccessToken OAuth2AccessToken="+token. toString());
		 log.info("storeAccessToken OAuth2Authentication="+authentication.toString());
		 
		 
		 Object[] params = new Object[] { authentication.getUserAuthentication().getName(), new SqlLobValue(serializeAuthentication(authentication)), token.getExpiration().toString() , token.getTokenType(), token.toString(), extractTokenKey(token.getValue()), extractTokenKey(token.getRefreshToken().toString()), token.getScope().toString(), new SqlLobValue(serializeAccessToken(token)) };
		 
		 int[] types = new int[] { Types.VARCHAR,Types.BLOB, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,Types.BLOB };
		 
		 int row = jdbcTemplate.update(INSERT_ACCESS_TOKEN_SQL, params, types);
		 
		 log.info(row + " row inserted.");

	}

	public void storeRefreshToken(OAuth2RefreshToken token,
			OAuth2Authentication authentication) {

		log.info("storeRefreshToken OAuth2AccessToken="+token.toString());
		log.info("storeRefreshToken OAuth2Authentication="+authentication.toString());
		
		Object[] params = new Object[] { authentication.getUserAuthentication().getName(), serializeAuthentication(authentication), token.getValue()};
		 
		 int[] types = new int[] { Types.VARCHAR,Types.BLOB, Types.VARCHAR};
		 
		 int row = jdbcTemplate.update(INSERT_REFRESH_TOKEN_SQL, params, types);
		 
		 log.info(row + " row inserted.");
	}
	
	
	
	protected String extractTokenKey(String value) {
 		if (value == null) {
			return null;
 		}
 		MessageDigest digest;
 		try {
 			digest = MessageDigest.getInstance("MD5");
 		}
 		catch (NoSuchAlgorithmException e) {
 			throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
 		}
 
 		try {
 			byte[] bytes = digest.digest(value.getBytes("UTF-8"));
 			return String.format("%032x", new BigInteger(1, bytes));
 		}
 		catch (UnsupportedEncodingException e) {
 			throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
 		}
 	}
	
	
 
 	public void removeAccessToken(String tokenValue) {
 		jdbcTemplate.update(deleteAccessTokenSql, extractTokenKey(tokenValue));
 	}
 
 
	
	protected byte[] serializeAuthentication(OAuth2Authentication authentication) {
		 		return SerializationUtils.serialize(authentication);
		 	}
	
	protected byte[] serializeAccessToken(OAuth2AccessToken token) {
		 		return SerializationUtils.serialize(token);
		 	}
		 
		 	protected byte[] serializeRefreshToken(OAuth2RefreshToken token) {
		 		return SerializationUtils.serialize(token);
		 	}
		 
		 
		 
		 	protected OAuth2AccessToken deserializeAccessToken(byte[] token) {
		 		return SerializationUtils.deserialize(token);
		 	}
		 
		 	protected OAuth2RefreshToken deserializeRefreshToken(byte[] token) {
		 		return SerializationUtils.deserialize(token);
		 	}
		 
		 	protected OAuth2Authentication deserializeAuthentication(byte[] authentication) {
		 		return SerializationUtils.deserialize(authentication);
		 	}
		 
		 	public void setInsertAccessTokenSql(String insertAccessTokenSql) {
		 		this.insertAccessTokenSql = insertAccessTokenSql;
		 	}
		 
		 	public void setSelectAccessTokenSql(String selectAccessTokenSql) {
		 		this.selectAccessTokenSql = selectAccessTokenSql;
		 	}
		 
		 	public void setDeleteAccessTokenSql(String deleteAccessTokenSql) {
		 		this.deleteAccessTokenSql = deleteAccessTokenSql;
		 	}
/*		 
		 	public void setInsertRefreshTokenSql(String insertRefreshTokenSql) {
		 		this.insertRefreshTokenSql = insertRefreshTokenSql;
		 	}
		 
		 	public void setSelectRefreshTokenSql(String selectRefreshTokenSql) {
		 		this.selectRefreshTokenSql = selectRefreshTokenSql;
		 	}
		 
		 	public void setDeleteRefreshTokenSql(String deleteRefreshTokenSql) {
		 		this.deleteRefreshTokenSql = deleteRefreshTokenSql;
		 	}
	*/	 
		 	public void setSelectAccessTokenAuthenticationSql(String selectAccessTokenAuthenticationSql) {
		 		this.selectAccessTokenAuthenticationSql = selectAccessTokenAuthenticationSql;
		 	}
		/* 
		 	public void setSelectRefreshTokenAuthenticationSql(String selectRefreshTokenAuthenticationSql) {
		 		this.selectRefreshTokenAuthenticationSql = selectRefreshTokenAuthenticationSql;
		 	}
		 
		 	public void setSelectAccessTokenFromAuthenticationSql(String selectAccessTokenFromAuthenticationSql) {
		 		this.selectAccessTokenFromAuthenticationSql = selectAccessTokenFromAuthenticationSql;
		 	}
		 
		 	public void setDeleteAccessTokenFromRefreshTokenSql(String deleteAccessTokenFromRefreshTokenSql) {
		 		this.deleteAccessTokenFromRefreshTokenSql = deleteAccessTokenFromRefreshTokenSql;
		 	}
		 
		 	public void setSelectAccessTokensFromUserNameSql(String selectAccessTokensFromUserNameSql) {
		 		this.selectAccessTokensFromUserNameSql = selectAccessTokensFromUserNameSql;
		 	}
		 
		 	public void setSelectAccessTokensFromUserNameAndClientIdSql(String selectAccessTokensFromUserNameAndClientIdSql) {
		 		this.selectAccessTokensFromUserNameAndClientIdSql = selectAccessTokensFromUserNameAndClientIdSql;
		 	}
		 
		 	public void setSelectAccessTokensFromClientIdSql(String selectAccessTokensFromClientIdSql) {
		 		this.selectAccessTokensFromClientIdSql = selectAccessTokensFromClientIdSql;
		 	}
		 	*/
}
