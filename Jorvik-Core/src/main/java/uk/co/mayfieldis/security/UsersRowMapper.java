package uk.co.mayfieldis.security;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class UsersRowMapper implements RowMapper<Users> {

        public Users mapRow(ResultSet rs, int rowNum) throws SQLException {

            Users user = new Users();

            user.setPassword(rs.getString("Password"));
            
            user.setUserName(rs.getString("Username"));
                        
            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            
            user.setItems(authorities);
            
            return user;
          
        }

}
