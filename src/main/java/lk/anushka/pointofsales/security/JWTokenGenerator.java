package lk.anushka.pointofsales.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lk.anushka.pointofsales.constant.SecurityConstant;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTokenGenerator {
    public String generateToken(Authentication authentication){
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + SecurityConstant.JWT_EXPIRATION);

        String token = Jwts.builder().
                setSubject(username).
                setIssuedAt(new Date()).
                setExpiration(expireDate).
                signWith(SignatureAlgorithm.HS512, SecurityConstant.JWT_SECRET).
                compact();
        return token;
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser().setSigningKey(SecurityConstant.JWT_SECRET).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
    public boolean  validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SecurityConstant.JWT_SECRET).parseClaimsJws(token);
            return true;
        } catch (Exception e){
            throw new AuthenticationCredentialsNotFoundException("JWT expired or incorrect");
        }
    }
    public Date getExpirationFromJWT(String token) {
        Claims claims = Jwts.parser().setSigningKey(SecurityConstant.JWT_SECRET).parseClaimsJws(token).getBody();
        return claims.getExpiration();
    }


}
