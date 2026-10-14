package com.backend.medicare.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret:MediCare_SuperSecret_Key_2026_Gourav@1234567890@@112@}")
    private String jwtSecret;

    @Value("${jwt.issuer:MediCareAPI}")
    private String jwtIssuer;

    @Value("${jwt.audience:MediCareClient}")
    private String jwtAudience;

    @Value("${jwt.expiry-minutes:60}")
    private int expiryMinutes;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(int userId, String email, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (long) expiryMinutes * 60 * 1000);

        return Jwts.builder()
                .issuer(jwtIssuer)
                .audience().add(jwtAudience).and()
                .subject(String.valueOf(userId))
                // Standard & .NET claim mappings for seamless cross-framework compatibility
                .claim("userId", userId)
                .claim("nameid", String.valueOf(userId))
                .claim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier", String.valueOf(userId))
                .claim("email", email)
                .claim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress", email)
                .claim("role", role)
                .claim("http://schemas.microsoft.com/ws/2008/06/identity/claims/role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Integer getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Object claimVal = claims.get("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier");
        if (claimVal != null) {
            return Integer.parseInt(claimVal.toString());
        }
        claimVal = claims.get("nameid");
        if (claimVal != null) {
            return Integer.parseInt(claimVal.toString());
        }
        claimVal = claims.get("userId");
        if (claimVal != null) {
            return Integer.parseInt(claimVal.toString());
        }
        return Integer.parseInt(claims.getSubject());
    }

    public String getEmailFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Object emailVal = claims.get("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress");
        if (emailVal != null) {
            return emailVal.toString();
        }
        emailVal = claims.get("email");
        return emailVal != null ? emailVal.toString() : "";
    }

    public String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Object roleVal = claims.get("http://schemas.microsoft.com/ws/2008/06/identity/claims/role");
        if (roleVal != null) {
            return roleVal.toString();
        }
        roleVal = claims.get("role");
        return roleVal != null ? roleVal.toString() : "";
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration() == null || claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
