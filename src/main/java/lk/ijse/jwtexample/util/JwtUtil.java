package lk.ijse.jwtexample.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lk.ijse.jwtexample.dto.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@PropertySource(ignoreResourceNotFound = true,value = "classpath:otherprops.properties")
public class JwtUtil implements Serializable {
    private static final long serialVersionUID = 234234523523L;
    public static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 12;
    @Value("${jwt.secret}")
    private String secretKey;

    public String getUsernameFromToken(String token){
        return getClaimFromToken(token, Claims :: getSubject);
    }
    public Claims getUserRoleCodeFromToken(String token){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    public Date getExpirationDataFromToken(String token){
        return getClaimFromToken(token,Claims::getExpiration);
    }
    public <T> T getClaimFromToken(String token , Function<Claims, T>claimsResolver){
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    private Claims getAllClaimsFromToken(String token){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }
    //    check if the token has expired
    private boolean isTokenExpired(String token){
        final Date expiration = getExpirationDataFromToken(token);
        return expiration.before(new Date());
    }

//    generat token from user
    public String generateToken(UserDTO userDTO){
        Map<String, Object> claims = new HashMap<>();
        claims.put("role",userDTO.getRole());
        return doGenerateToken(claims, userDTO.getEmail());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secretKey).compact();
    }
    public boolean validateToken(String token, UserDetails userDetails){
        final String username = getUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}
