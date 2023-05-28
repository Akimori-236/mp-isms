package tfip.akimori.server.services;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import tfip.akimori.server.models.User;

@Service
public class JwtService {

    // 3 hours
    private static final Long expDuration = 1000l * 60 * 60 * 3;

    @Value("${sha256.secret}")
    private String sha256Secret;

    public JsonObject generateJWT(User user) {
        // create JWT
        String jwt = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setIssuer("ISMS")
                .setSubject(user.getEmail())
                .claim("givenname", user.getGivenname())
                .claim("familyname", user.getFamilyname())
                .claim("picture", user.getPicture())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + expDuration))
                .signWith(Keys.hmacShaKeyFor(sha256Secret.getBytes()), SignatureAlgorithm.HS256) // sign with secret key
                .compact();
        // create JSON object
        JsonObject jwtObject = Json.createObjectBuilder()
                .add("jwt", jwt)
                .build();
        return jwtObject;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // generic method
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(sha256Secret.getBytes());
    }

    // generate only with userdetails
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 60)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Boolean isTokenExpired(String token) {
        // return extractExpiration(token).before(new Date());
        return false;
    }

    public Boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) &&
                !isTokenExpired(token);
    }
}
