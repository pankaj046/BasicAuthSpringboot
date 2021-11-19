package sharma.pankaj.auth.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import sharma.pankaj.auth.config.service.AuthUserDetails;
import sharma.pankaj.auth.exception.CustomExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Component
public class JWTTokenProviders {
    @Value("${jwt.secret}")
    private String secret;

    public String generateJwtToken(AuthUserDetails authUserDetails) {
        String[] claims = getClaimsFromUser(authUserDetails);
        return JWT.create().withIssuer("PANKAJ_SHARMA")
                .withAudience("ADMIN_AUTH")
                .withIssuedAt(new Date())
                .withSubject(authUserDetails.getUsername())
                .withArrayClaim("CLAIMS", claims)
                .withExpiresAt(new Date((new Date().getTime()) + 8 * 24 * 60 * 60 * 1000))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    public List<GrantedAuthority> getAuthorities(String token) {
        String[] claims = getClaimsFromToken(token);
        return stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public Authentication getAuthentication(String username,
                                            List<GrantedAuthority> authorities,
                                            HttpServletRequest request) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(username,
                null, authorities);
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return usernamePasswordAuthenticationToken;
    }

    public boolean isTokenValid(String username, String token){
        JWTVerifier verifier = getJWTVerifier();
        return StringUtils.isNotEmpty(username) && !isTokenExpired(verifier, token);
    }

    public String getSubject(String token){
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getSubject();
    }

    private boolean isTokenExpired(JWTVerifier verifier, String token) {
        Date expiration = verifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }

    private String[] getClaimsFromToken(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getClaim("CLAIMS").asArray(String.class);
    }

    private JWTVerifier getJWTVerifier() {
        JWTVerifier verifier;
        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);
            verifier = JWT.require(algorithm)
                    .withIssuer("PANKAJ_SHARMA")
                    .build();

        } catch (JWTVerificationException e) {
            throw new CustomExceptionHandler("Token verification is Expired!");
        }
        return verifier;
    }

    private String[] getClaimsFromUser(AuthUserDetails authUserDetails) {
        List<String> auth = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : authUserDetails.getAuthorities()) {
            auth.add(grantedAuthority.getAuthority());
        }
        return auth.toArray(new String[0]);
    }

}
