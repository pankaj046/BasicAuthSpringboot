package sharma.pankaj.auth.config.jwt;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import sharma.pankaj.auth.config.service.AuthUserDetailsService;
import sharma.pankaj.auth.exception.CustomExceptionHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@Component
public class JWTFilters extends OncePerRequestFilter {

    private final JWTTokenProviders jwtTokenProviders;
    private final AuthUserDetailsService authUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getMethod().equalsIgnoreCase("OPTIONS")){
            response.setStatus(HttpServletResponse.SC_OK);
        }else {
//            String jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJhdWQiOiJBRE1JTl9BVVRIIiwic3ViIjoicGFua2FqMUBnbWFpbC5jb20iLCJDTEFJTVMiOlsiUk9MRV9VU0VSIiwiUkVBRF9QUklWSUxFR0UiXSwiaXNzIjoiUEFOS0FKX1NIQVJNQSIsImV4cCI6MTYzODAzNTc5NSwiaWF0IjoxNjM3MzQ0NTk1fQ.VwXXmQs86t30OKvTrXvVsVtYJPbu3xpzp-gDV_PaXVvdTq_9vUlpElhmH9xGuv61UfKEeRYXDAJ0_SAf1LU-aA";

            String jwtToken = getTokenFromRequest(request);
            if (!jwtToken.isEmpty() && StringUtils.hasText(jwtToken) && jwtTokenProviders.isTokenValid(jwtTokenProviders.getSubject(jwtToken), jwtToken)) {
                String username = jwtTokenProviders.getSubject(jwtToken);
                UserDetails userDetails = authUserDetailsService.loadUserByUsername(username);
                List<GrantedAuthority> authorities = jwtTokenProviders.getAuthorities(jwtToken);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            }else {
                throw new CustomExceptionHandler("Invalid token");
            }
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        System.out.println(request.getServletPath());
        return new AntPathMatcher().match("/api/auth/**", request.getServletPath());
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return "";
    }
}
